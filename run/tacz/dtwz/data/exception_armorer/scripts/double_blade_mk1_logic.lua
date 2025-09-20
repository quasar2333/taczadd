local M = {}

function M.calcSpread(api, num, spread)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0
        }
    end
    -- 根据计数器返回弹道形状
    if (cache.melee_light_count == 0) then
        return{(((-1)^num)*num)/4,-((-1)^num*num)/24}
    elseif (cache.melee_light_count == 1) then
        return{-num/1.5,-num/4.5}
    elseif (cache.melee_light_count == 100) then
        return{num/1.5,num/4.5}
    elseif (cache.melee_light_count == 2) then
        return{(((-1)^num)*num)/5,-((-1)^num*num)/10}
    elseif (cache.melee_light_count == 3) then
        return{-num/1.5,-num/2}
    elseif (cache.melee_light_count == 300) then
        return{num/1.5,num/2}
    elseif (cache.melee_light_count == 4) then
        return{(((-1)^num)*num)/8,-((-1)^num*num)/6}
    elseif (cache.melee_light_count == 400) then
        return{num/2.5,num/2}
    elseif (cache.melee_light_count == 401) then
        return{-num/2.5,-num/2}
    end
end

function M.shoot(api)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0
        }
    end
    -- 重置计数器
    local last_shoot_timestamp = api:getLastShootTimestamp()
    local current_timestamp = api:getCurrentTimestamp()
    local shoot_interval = api:getShootInterval()
    if (current_timestamp - last_shoot_timestamp > shoot_interval + 400) then
        cache.melee_light_count = 0
    end
    -- 初始化射速计数器
    if (api:getShootInterval() >= 400) then
        api:adjustShootInterval(300-api:getShootInterval())
    end
    -- 攻击
    if (cache.melee_light_count == 0) then
        api:shootOnce(false)
        cache.melee_light_count = 1
    elseif (cache.melee_light_count == 1) then
        api:shootOnce(false)
        cache.melee_light_count = 100
        api:shootOnce(false)
        cache.melee_light_count = 2
    elseif (cache.melee_light_count == 2) then
        api:shootOnce(false)
        cache.melee_light_count = 3
    elseif (cache.melee_light_count == 3) then
        api:shootOnce(false)
        cache.melee_light_count = 300
        api:shootOnce(false)
        cache.melee_light_count = 4
    elseif (cache.melee_light_count == 4) then
        api:shootOnce(false)
        cache.melee_light_count = 400
        api:shootOnce(false)
        cache.melee_light_count = 401
        api:shootOnce(false)
        cache.melee_light_count = 0
        api:adjustShootInterval(300)
    end
    api:cacheScriptData(cache)
end

return M