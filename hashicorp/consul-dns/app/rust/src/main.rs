// mod consul;

use actix_web::{App, get, HttpServer, Responder};
use tokio;

#[get("/")]
async fn greet() -> impl Responder {
    format!("Hello, Consul service-discovered client! I am Rust.")
}

#[tokio::main]
async fn main() -> std::io::Result<()> {
    HttpServer::new(|| {
        App::new().service(greet)
    })
        .bind(("127.0.0.1", 9000))?
        .run()
        .await
}
