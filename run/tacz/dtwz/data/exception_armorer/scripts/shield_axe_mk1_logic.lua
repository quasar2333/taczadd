local M = {}

function M.calcSpread(api, num, spread)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_heavy_count = 0
        }
    end
    -- 根据计数器返回弹道形状
    if (api:getFireMode() == BURST) then
        -- 轻击
        if (cache.melee_light_count == 0) then
            return{(((-1)^num)*num)/3*(0.6+num/25), -(-1)^num*num/28*(0.6+num/25)}
        elseif (cache.melee_light_count == 1) then
            return{(((-1)^num)*num)/3*(0.6+num/25), (-1)^num*num/28*(0.6+num/25)}
        elseif (cache.melee_light_count == 2) then
            return{(((-1)^num)*num)/5*(0.6+num/25), -(-1)^num*num/5*(0.6+num/25)}
        elseif (cache.melee_light_count == 3) then
            return{(((-1)^num)*num)/5*(0.6+num/25), (-1)^num*num/5*(0.6+num/25)}
        end
    elseif (api:getFireMode() == SEMI) then
        -- 重击
        if (cache.melee_heavy_count == 1) then
            return{(((-1)^num)*num)/3*(0.6+num/25), (-1)^num*num/28*(0.6+num/25)}
        end
    end
end

function M.shoot(api)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_heavy_count = 0
        }
    end
    -- 重置计数器
    local last_shoot_timestamp = api:getLastShootTimestamp()
    local current_timestamp = api:getCurrentTimestamp()
    local shoot_interval = api:getShootInterval()
    if (current_timestamp - last_shoot_timestamp > shoot_interval + 500) then
        cache.melee_light_count = 0
        cache.melee_heavy_count = 0
    end
    if (api:getFireMode() == SEMI) then
        if (cache.melee_heavy_count == 0) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_heavy_count = 1
                return false end,2500,0,1)
        elseif (cache.melee_heavy_count == 1) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_heavy_count = 0
                return false end,2500,0,1)
        end
    elseif (api:getFireMode() == BURST) then
        if (cache.melee_light_count == 0) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_light_count = 1
                return false end,800,0,1)
        elseif (cache.melee_light_count == 1) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_light_count = 2
                return false end,800,0,1)
        elseif (cache.melee_light_count == 2) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_light_count = 3
                return false end,800,0,1)
        elseif (cache.melee_light_count == 3) then
            api:safeAsyncTask(function ()
                api:shootOnce(false)
                cache.melee_light_count = 0
                return false end,800,0,1)
        end
    end
    api:cacheScriptData(cache)
end

return M