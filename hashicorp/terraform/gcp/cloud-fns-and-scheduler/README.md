# Cloud Functions and Cloud Scheduler

`gcloud.sh` sets up a nodejs function to be invoked on a cron schedule and begins with enabling required GCP services
for scheduling, pubsub and cloud functions.

`gcloud functions deploy` is ran from the project's root directory next to `package.json`.

## Cloud Function arguments

The `gcloud scheduler jobs create` command's `--message-body="{}"` attribute will be parsed into a JS object from JSON
and used for the function invocation's first arg.
Specifying comma-separated attributes with `--attributes="key=value"` will invoke the function with a PubsubMessage for
the first arg:

```json
{
  "@type": "type.googleapis.com/google.pubsub.v1.PubsubMessage",
  "attributes": {
    "key": "value"
  },
  "data": ""
}
```

The second argument to an invocation will be a [Cloud Events](https://cloud.google.com/eventarc/docs/cloudevents) object
detailing the invocation trigger:

```json
{
  "eventId": "5403224006767547",
  "timestamp": "2022-08-20T20:39:00.592Z",
  "eventType": "google.pubsub.topic.publish",
  "resource": {
    "service": "pubsub.googleapis.com",
    "name": "projects/eighty4-learning/topics/fomo-plot-weekly",
    "type": "type.googleapis.com/google.pubsub.v1.PubsubMessage"
  }
}
```

📌 Cloud Functions needs your `package.json` `main` attribute to reference a full filename path with extension

## gcloud commands

- view logs from Cloud Function executions:

```
# all execs
gcloud functions logs read $FN_NAME --project $PROJECT_NAME

# or specific exec
gcloud functions logs read $FN_NAME --execution-id=$EXECUTION_ID --project $PROJECT_NAME
```
