#include <format>
#include "hsl.h"
#include "gtest/gtest.h"

TEST(Hsl, ToCssFn) {
    auto hsl = parse_rgb("aabbcc");
    ASSERT_EQ(std::format("{:.0f}", hsl.h), "210");
    ASSERT_EQ(std::format("{:.2f}", hsl.s), "0.25");
    ASSERT_EQ(std::format("{:.2f}", hsl.l), "0.73");
}
