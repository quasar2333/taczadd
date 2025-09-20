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
        return{(((-1)^num)*num)/3*(0.6+num/25),-((-1)^num*num)/18*(0.6+num/25)}
    elseif (cache.melee_light_count == 1) then
        return{(((-1)^num)*num)/3*(0.6+num/25),((-1)^num*num)/24*(0.6+num/25)}
    elseif (cache.melee_light_count == 2) then
        return{(((-1)^num)*num)/36*(0.6+num/25),-((-1)^num*num)/4.5*(0.6+num/25)}
    elseif (cache.melee_light_count == 3) then
        return{(((-1)^num)*num)/4*(0.6+num/25),-((-1)^num*num)/5*(0.6+num/25)}
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
    if (current_timestamp - last_shoot_timestamp > shoot_interval + 300) then
        cache.melee_light_count = 0
    end
    -- 初始化射速计数器
    if (api:getShootInterval() >= 600) then
        api:adjustShootInterval(500-api:getShootInterval())
    end
    -- 攻击
    if (cache.melee_light_count == 0) then
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            cache.melee_light_count = 1
            return false
        end,800,0,1)
    elseif (cache.melee_light_count == 1) then
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            cache.melee_light_count = 2
            return false
        end,800,0,1)
    elseif (cache.melee_light_count == 2) then
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            cache.melee_light_count = 3
            return false
        end,800,0,1)
    elseif (cache.melee_light_count == 3) then
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            cache.melee_light_count = 0
            return false
        end,800,0,1)
    end
    api:cacheScriptData(cache)
end

return M