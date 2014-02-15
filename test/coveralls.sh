CLOVERAGE_VERSION='1.0.4-SNAPSHOT' lein2 cloverage -o cov --coveralls
curl -F 'json_file=@cov/coveralls.json' 'https://coveralls.io/api/v1/jobs'
