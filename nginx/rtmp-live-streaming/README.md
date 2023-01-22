# NGINX RTMP server to stream a live feed to browser supported MPEG-DASH and HLS protocols

In `nginx.conf`, HLS and MPEG-DASH are enabled in `rtmp::server::application`. `h.264` video streamed to port 1935 will be written as HLS and MPEG-DASH chunks to `/nginx/data` within the container. The HLS and MPEG-DASH manifests and video chunks are served from `/hls` and `/dash` configured in `http::server` and are ready for a browser's `<video/>` element once written to `/nginx/data`.

Configure webhooks `on_publish`, `on_play` and `on_done` to handle a new stream or streamer within an application. Streaming can be authorized by returning a 2XX response or rejected by returning a 4XX or 5XX.

```shell
docker build -t rtmp-server .
docker run --name rtmp -v nginx.conf:/nginx/config/nginx.conf:ro -v index.html:/nginx/static/index.html:ro -p 1935:1935 -p 8080:8080 rtmp-server
```

``` shell
ffmpeg -f x11grab -s 1920x1200 -framerate 15 -i :0.0 -c:v libx264 -preset fast -pix_fmt yuv420p -s 1280x800 -threads 0 -f flv "rtmp://localhost:1935/screencast/my-stream"
```

nginx launches a transmuxing ffmpeg process inside the container for each streaming connection it receives on port 1935. A working live stream requires the nginx/RTMP server, a separate ffmpeg process streaming to nginx and a browser with a `<video/>` element sourcing the stream via nginx.

Running all three components on the same machine had an approx. 5-8 second delay for the live stream. `rtmp::server::application::exec` can be used to provide a customized ffmpeg command or use a different binary.

[Read more about the nginx module for the RTMP protocol on GitHub.](https://github.com/arut/nginx-rtmp-module)

[Read about ffmpeg streaming from their guide.](https://trac.ffmpeg.org/wiki/StreamingGuide)
