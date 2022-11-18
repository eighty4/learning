use std::convert::Infallible;
use std::net::SocketAddr;

use hyper::{Body, Request, Response, Server};
use hyper::service::{make_service_fn, service_fn};
use structopt::StructOpt;

#[derive(StructOpt)]
#[structopt(name = "clifeld", about = "a cli about nothing")]
struct CliOpts {
    #[structopt(short, long)]
    debug: bool,
}

async fn hey(_req: Request<Body>) -> Result<Response<Body>, Infallible> {
    Ok(Response::new("Hello, World".into()))
}

#[tokio::main]
pub async fn main() {
    let opt = CliOpts::from_args();
    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));

    let server = Server::bind(&addr)
        .serve(make_service_fn(|_conn| async {
            Ok::<_, Infallible>(service_fn(hey))
        }));

    if opt.debug {
        println!("listening on {}", addr);
    }

    if let Err(e) = server.await {
        eprintln!("server error: {}", e);
    }
}
