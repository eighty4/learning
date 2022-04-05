{
  "targets": [
    {
      "target_name": "ping",
      "cflags!": [ "-fno-exceptions" ],
      "cflags_cc!": [ "-fno-exceptions" ],
      "sources": [
        "ping.cc"
      ],
      "include_dirs": [
        "<!@(node -p \"require('node-addon-api').include_dir\")"
      ],
      'defines': [ 'NAPI_DISABLE_CPP_EXCEPTIONS' ],
    }
  ]
}
