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
        return{0,0}
    elseif (cache.melee_light_count == 1) then
        return{(((-1)^num)*num)/2,((-1)^num*num)/10}
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
    if (current_timestamp - last_shoot_timestamp > shoot_interval + 250) then
        cache.melee_light_count = 0
    end
    -- 攻击
    if (cache.melee_light_count == 0) then
        -- 第一击：戳刺
        api:safeAsyncTask(function ()
            cache.melee_light_count = 0
            api:shootOnce(false)
            cache.melee_light_count = 1
            return false
        end,4000,0,1)
    elseif (cache.melee_light_count == 1) then
        -- 第二击：横砍
        api:safeAsyncTask(function ()
            cache.melee_light_count = 1
            api:shootOnce(false)
            cache.melee_light_count = 0
            return false
        end,4000,0,1)
    end
    -- 写回缓存
    api:cacheScriptData(cache)
end

return M