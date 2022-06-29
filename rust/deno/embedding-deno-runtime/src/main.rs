use std::process;
use std::rc::Rc;
use std::sync::Arc;

use deno_core::error::AnyError;
use deno_core::resolve_url_or_path;
use deno_runtime::BootstrapOptions;
use deno_runtime::deno_broadcast_channel::InMemoryBroadcastChannel;
use deno_runtime::deno_web::BlobStore;
use deno_runtime::permissions::Permissions;
use deno_runtime::worker::{MainWorker, WorkerOptions};

#[tokio::main]
async fn main() -> Result<(), AnyError> {
    let main_module = resolve_url_or_path("./throw-pid-via-error.js")?;
    let permissions = Permissions::default();

    let worker_opts = WorkerOptions {
        bootstrap: BootstrapOptions {
            args: vec![],
            cpu_count: 1,
            debug_flag: false,
            enable_testing_features: false,
            location: None,
            no_color: true,
            is_tty: false,
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: false,
            user_agent: "x".to_string(),
        },
        extensions: vec![],
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        seed: None,
        format_js_error_fn: None,
        source_map_getter: None,
        web_worker_preload_module_cb: Arc::new(|_| unreachable!()),
        create_web_worker_cb: Arc::new(|_| unreachable!()),
        maybe_inspector_server: None,
        should_break_on_first_statement: false,
        module_loader: Rc::new(deno_core::FsModuleLoader),
        get_error_class_fn: None,
        origin_storage_dir: None,
        blob_store: BlobStore::default(),
        broadcast_channel: InMemoryBroadcastChannel::default(),
        shared_array_buffer_store: None,
        compiled_wasm_module_store: None,
        stdio: Default::default(),
    };


    let mut worker = MainWorker::bootstrap_from_options(
        main_module.clone(), permissions, worker_opts);
    let result = worker.execute_main_module(&main_module).await;

    if let Err(e) = result {
        println!("js pid: {}, rust pid: {}",
                 e.to_string().split_whitespace().nth(1).unwrap(),
                 process::id());
    }

    Ok(())
}
