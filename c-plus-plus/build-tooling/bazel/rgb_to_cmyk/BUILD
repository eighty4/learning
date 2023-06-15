load("@rules_cc//cc:defs.bzl", "cc_binary")

cc_binary(
    name = "rgb_to_cmyk",
    srcs = ["rgb_to_cmyk.cc"],
    deps = ["//libcmyk:libcmyk"],
    copts = ["-std:c++20"],
)
