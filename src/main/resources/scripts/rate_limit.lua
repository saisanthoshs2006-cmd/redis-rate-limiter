local current = redis.call('GET', KEYS[1])

if not current then
    redis.call('SET', KEYS[1], 1)
    redis.call('EXPIRE', KEYS[1], ARGV[2])
    return 1
end

if tonumber(current) >= tonumber(ARGV[1]) then
    return 0
end

return redis.call('INCR', KEYS[1])