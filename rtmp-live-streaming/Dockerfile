FROM debian:bullseye-slim as build

ARG NGINX_VERSION=1.21.3
ARG NGINX_CONFIGURE
ARG NGINX_RTMP_VERSION=1.2.2
ARG OPENSSL_VERSION=3.0.0

ENV DEBIAN_FRONTEND noninteractive

WORKDIR /

RUN set -x \
    && apt-get update \
    && apt-get install -y build-essential wget \
    && wget https://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz \
    && tar zxf nginx-${NGINX_VERSION}.tar.gz \
    && wget https://github.com/arut/nginx-rtmp-module/archive/refs/tags/v${NGINX_RTMP_VERSION}.tar.gz -O nginx-rtmp-module-${NGINX_RTMP_VERSION}.tar.gz \
    && tar zxf nginx-rtmp-module-${NGINX_RTMP_VERSION}.tar.gz \
    && wget https://www.openssl.org/source/openssl-${OPENSSL_VERSION}.tar.gz \
    && tar zxf openssl-${OPENSSL_VERSION}.tar.gz

WORKDIR /nginx-${NGINX_VERSION}

RUN ./configure \
    ${NGINX_CONFIGURE} \
    --conf-path=/nginx/config/nginx.conf \
    --error-log-path=/nginx/logs/error.log \
    --http-log-path=/nginx/logs/access.log \
    --without-http_rewrite_module \
    --without-http_gzip_module \
    --with-openssl=../openssl-${OPENSSL_VERSION} \
    --add-module=../nginx-rtmp-module-${NGINX_RTMP_VERSION} \
    && make \
    && make install

FROM debian:bullseye-slim

ENV PATH $PATH:/usr/local/nginx/sbin

EXPOSE 1935
EXPOSE 8080

WORKDIR /

RUN addgroup --system --gid 101 nginx \
    && adduser --system --disabled-login --ingroup nginx --no-create-home --gecos "nginx user" --shell /bin/false --uid 101 nginx

COPY --from=build /usr/local/nginx /usr/local/nginx

RUN mkdir -p /nginx/config /nginx/logs /nginx/data/hls /nginx/data/dash /nginx/static \
    && chown -R nginx:nginx /nginx /usr/local/nginx \
    && echo "<div>not a page</div>" > /nginx/static/index.html

USER 101

CMD ["nginx", "-g", "daemon off;"]
