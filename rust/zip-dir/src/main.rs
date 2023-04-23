use std::{fs, io};
use std::fs::File;
use std::io::{Read, Write};
use std::path::{Path, PathBuf};
use std::process::exit;

use zip::write::FileOptions;
use zip::ZipWriter;

fn main() {
    let filename = "archive.zip";
    fs::remove_file(filename).ok();
    let args: Vec<_> = std::env::args().collect();
    let dir = if args.len() > 1 {
        PathBuf::from(args[1].clone())
    } else {
        println!("dir input not provided:\n\n    zip-dir DIR_NAME");
        exit(1);
    };
    if !dir.is_dir() {
        println!("dir input is not a dir: {}", dir.to_string_lossy());
        exit(1);
    } else {
        println!("zipping dir {} into {filename}", dir.to_string_lossy());
        zip_dir(&dir, filename.to_string());
    }
}

fn zip_dir(dir: &Path, filename: String) {
    match dir.canonicalize() {
        Err(err) => {
            println!("{err}");
            exit(1);
        }
        Ok(dir) => {
            let zipfile = File::create(filename).expect("create file");
            let mut zip = ZipWriter::new(zipfile);
            let mut paths: Vec<PathBuf> = Vec::new();
            collect_paths(&dir, &mut paths).expect("");
            paths.sort();
            let mut buf = Vec::new();
            let options = FileOptions::default()
                .compression_method(zip::CompressionMethod::Deflated);
            for path in paths {
                let rel_path = path.strip_prefix(&dir).expect("rel path");
                if path.is_dir() {
                    zip.add_directory(rel_path.to_string_lossy(), options).expect("add dir");
                } else {
                    let mut f = File::open(&path).expect("open file");
                    f.read_to_end(&mut buf).expect("read file");
                    zip.start_file(rel_path.to_string_lossy(), options).expect("start file");
                    zip.write_all(buf.as_ref()).expect("write file");
                    buf.clear();
                }
            }
            zip.finish().expect("write file");
        }
    }
}

fn collect_paths(dir: &Path, paths: &mut Vec<PathBuf>) -> io::Result<()> {
    if dir.is_dir() {
        for entry in fs::read_dir(dir)? {
            let entry = entry?;
            let path = entry.path();
            paths.push(path.clone());
            if path.is_dir() {
                collect_paths(&path, paths)?;
            }
        }
    }
    Ok(())
}
