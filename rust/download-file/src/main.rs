use std::fs::File;
use std::io::{copy, Cursor};

use error_chain::error_chain;

error_chain! {
     foreign_links {
         Io(std::io::Error);
         HttpRequest(reqwest::Error);
     }
}

#[tokio::main]
async fn main() -> Result<()> {
    let url = "https://raw.githubusercontent.com/eighty4/banjo/main/web/favicon.ico";
    let response = reqwest::get(url).await?;
    let mut file = {
        let filename = response
            .url()
            .path_segments()
            .and_then(|segments| segments.last())
            .and_then(|name| if name.is_empty() { None } else { Some(name) })
            .unwrap_or("favicon.ico");

        let filename = dirs::download_dir().unwrap().join(filename);
        println!("{:?}", filename);
        File::create(filename)?
    };
    let mut bytes = Cursor::new(response.bytes().await?);
    copy(&mut bytes, &mut file)?;
    Ok(())
}
