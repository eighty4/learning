load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "libcmyk",
    srcs = ["cmyk.cc"],
    hdrs = ["cmyk.h"],
    copts = ["-std:c++20"],
    visibility = ["//rgb_to_cmyk:__pkg__"],
)
