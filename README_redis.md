### redis起動

```
docker run --rm -p 6379:6379 -d --name redis redis
```

このRedisサーバで疎通確認するために、別途Redisのコンテナを立ててそこのcliで接続確認してみます。

```
$ docker container run --rm --link redis:REDIS_HOST -d --name redis1 redis
00ede7784705
$ docker exec -it 00e /bin/bash
$ root@00ede7784:/data# 
```

で別のredisを立ち上げ、docker exec してコンテナに入りました

```
root@00ede7784:/data# redis-cli -h REDIS_HOST
REDIS_HOST:6379> keys *
1) "spring:session:sessions:cda39601-5417-4d54-88f3-8923862b8474"
REDIS_HOST:6379> get "spring:session:sessions:cda39601-5417-4d54-88f3-8923862b8474"
(error) WRONGTYPE Operation against a key holding the wrong kind of value

REDIS_HOST:6379> set "aaa" test
OK
REDIS_HOST:6379> get "aaa"
"test"
REDIS_HOST:6379> quit

root@00ede7784:/data# 
```

疎通できました。以上

