gcloud services enable cloudscheduler.googleapis.com pubsub.googleapis.com cloudfunctions.googleapis.com

gcloud pubsub topics create fomo-plot-weekly --project=eighty4-learning

gcloud scheduler jobs create pubsub fomo-plot-weekly-schedule
    --location=us-central1 \
    --schedule="15 3 * * 6" \
    --topic=projects/eighty4-learning/topics/fomo-plot-weekly \
    --time-zone="US/Central" \
    --project=eighty4-learning \
    --message-body="{}"

gcloud functions deploy weekly \
    --runtime nodejs16 \
    --trigger-topic fomo-plot-weekly \
    --region us-central1 \
    --project eighty4-learning
