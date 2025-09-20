---@diagnostic disable: trailing-space
local M = {}

function M.calcSpread(api, num, spread)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_static_count = 0
        }
    end
    if (api:getFireMode() == AUTO) then
        if (num == 0) then
            return{0,0}
        end
    end
    if (api:getFireMode() == SEMI) then
        -- 根据计数器返回弹道形状
        if (cache.melee_light_count == 0) then
            return{(((-1)^num)*num)/15,-((-1)^num*num)/3}
        elseif (cache.melee_light_count == 1) then
            return{-(((-1)^num)*num)/15,-((-1)^num*num)/3}
        end
    end
end

function M.shoot(api)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_static_count = 0
        }
    end
    -- 重置计数器
    local last_shoot_timestamp = api:getLastShootTimestamp()
    local current_timestamp = api:getCurrentTimestamp()
    local shoot_interval = api:getShootInterval()
    if (api:getFireMode() == SEMI) then
        if (current_timestamp - last_shoot_timestamp > shoot_interval + 400) then
            cache.melee_light_count = 0
            cache.melee_static_count = 0
        end
    end
    if (api:getFireMode() == AUTO) then
        if (current_timestamp - last_shoot_timestamp > shoot_interval + 100) then
            cache.melee_light_count = 0
            cache.melee_static_count = 0
        end
    end
    -- 初始化射速计数器
    if (api:getShootInterval() >= 400) then
        api:adjustShootInterval(300-api:getShootInterval())
    end
    -- 攻击
    if (api:getFireMode() == SEMI) then
        if (cache.melee_light_count == 0) then
            api:safeAsyncTask(function ()
                cache.melee_light_count = 0
                api:shootOnce(false)
                cache.melee_light_count = 1
                return false
            end,1200,0,1)
        elseif (cache.melee_light_count == 1) then
            api:safeAsyncTask(function ()
                cache.melee_light_count = 1
                api:shootOnce(false)
                cache.melee_light_count = 0
                return false
            end,1200,0,1)
        end
    elseif (api:getFireMode() == AUTO) then
        cache.melee_static_count = cache.melee_static_count + 1
        if (cache.melee_static_count >= 6) then
            api:shootOnce(false)
        end
    end
    api:cacheScriptData(cache)
end

return M