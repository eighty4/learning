resource "aws_route53_zone" "eighty4-io-hosted-zone" {
  name = "eighty4.io."
}

//resource "aws_route53_record" "ci-eighty4-io-route" {
//  zone_id = aws_route53_zone.eighty4-io-hosted-zone.zone_id
//  name = "ci.inf.eighty4.io."
//  type = "A"
//
//  alias {
//    name = aws_lb.eighty4-inf-teamcity-lb.dns_name
//    zone_id = aws_lb.eighty4-inf-teamcity-lb.zone_id
//    evaluate_target_health = true
//  }
//}

resource "aws_route53_record" "orchestrate-dev-screem-eighty4-io-route" {
  zone_id = aws_route53_zone.eighty4-io-hosted-zone.zone_id
  name = "orchestrate.dev.screem.eighty4.io."
  type = "A"

  alias {
    name = aws_lb.eighty4-dev-screem-api-orchestrate.dns_name
    zone_id = aws_lb.eighty4-dev-screem-api-orchestrate.zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "proxy-dev-screem-eighty4-io-route" {
  zone_id = aws_route53_zone.eighty4-io-hosted-zone.zone_id
  name = "proxy.dev.screem.eighty4.io."
  type = "A"

  alias {
    name = aws_lb.eighty4-dev-screem-api-proxy.dns_name
    zone_id = aws_lb.eighty4-dev-screem-api-proxy.zone_id
    evaluate_target_health = true
  }
}
