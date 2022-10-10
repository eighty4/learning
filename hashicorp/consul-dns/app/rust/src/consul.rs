use consulrs::api::service::requests::RegisterServiceRequest;
use consulrs::client::{ConsulClient, ConsulClientSettingsBuilder};
use consulrs::service;

// todo sigterm deregisters from consul
//  https://dev.to/talzvon/handling-unix-kill-signals-in-rust-55g6

async fn register_with_consul() {
    let consul = ConsulClient::new(
        ConsulClientSettingsBuilder::default()
            .address("https://127.0.0.1:8200")
            .build()
            .unwrap()
    ).unwrap();

    service::register(
        &consul,
        "my_service",
        Some(
            RegisterServiceRequest::builder()
                .address("192.168.56.10")
                .port(9000),
        ))
        .await
        .expect("");
}
