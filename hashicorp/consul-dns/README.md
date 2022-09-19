
## Vagrant

```bash
vagrant up
```

```bash
consul members -http-addr=192.168.56.8:8500
```

```bash
consul catalog nodes -service=nodejs-app -detailed -http-addr=192.168.56.8:8500
```

```bash
vagrant ssh app-js -c "node /app/app.js"
```

## DNS

`systemctl --version` must show 246 or higher which is true of Debian 11

```bash
resolvectl domain
```

```bash
resolvectl query consul.service.consul
```

## GCP

```bash
# from //image
packer build consul.pkr.hcl
```

```bash
# from //
terraform apply
```
