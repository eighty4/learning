use std::rc::Rc;

use deno_core::error::AnyError;
use deno_core::FsModuleLoader;
use deno_core::JsRuntime;
use deno_core::RuntimeOptions;
use deno_core::url::Url;
use deno_core::v8;
use deno_core::v8::Local;

#[tokio::main]
async fn main() -> Result<(), AnyError> {
    let opts = RuntimeOptions {
        module_loader: Some(Rc::new(FsModuleLoader)),
        ..Default::default()
    };
    let mut runtime = JsRuntime::new(opts);
    let module_url_str = format!(
        "file://{}",
        std::env::current_dir().unwrap()
            .join("export-deno-props.js")
            .to_str().unwrap());
    let module_url = Url::parse(module_url_str.as_str())?;
    let module_id = runtime.load_main_module(&module_url, None).await?;
    let _ = runtime.mod_evaluate(module_id);
    runtime.run_event_loop(false).await?;

    let global = runtime.get_module_namespace(module_id)?;
    let isolate = runtime.v8_isolate();
    let object = global.open(isolate);
    let scope = &mut (runtime.handle_scope());

    {
        println!("\nwith property names");
        let has_deno_api_name = Local::from(v8::String::new(scope, "hasDenoApi").unwrap());
        let has_deno_api_value = object.get(scope, has_deno_api_name).unwrap().to_rust_string_lossy(scope);
        println!("hasDenoApi = {}", has_deno_api_value);
        let deno_properties_name = Local::from(v8::String::new(scope, "denoProperties").unwrap());
        let deno_properties_value = object.get(scope, deno_properties_name).unwrap().to_rust_string_lossy(scope);
        println!("denoProperties = {}", deno_properties_value);
    }

    {
        println!("\nwith array index");
        let prop_names = object.get_property_names(scope).unwrap();
        for i in 0..prop_names.length() {
            let prop_name = prop_names.get_index(scope, i).unwrap();
            let prop_value = object.get(scope, prop_name).unwrap();
            println!("{} = {}", prop_name.to_rust_string_lossy(scope), prop_value.to_rust_string_lossy(scope));
        }
    }

    Ok(())
}
