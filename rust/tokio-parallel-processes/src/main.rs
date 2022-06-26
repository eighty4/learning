use std::time::Instant;

use tokio::{join, process::Command};

async fn sleep(seconds: i8) -> i8 {
    let mut cmd = Command::new("sleep")
        .arg(seconds.to_string())
        .spawn()
        .expect("failed to spawn");
    let _ = cmd.wait().await;
    seconds
}

#[tokio::main]
async fn main() {
    let start = Instant::now();

    let (one, two, three) = join!(sleep(1), sleep(3), sleep(5));

    let duration = start.elapsed();
    println!("completed {}s, {}s and {}s sleeps in {:?}", one, two, three, duration);
}
