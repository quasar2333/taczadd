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
    if (cache.melee_light_count == 1 or cache.melee_light_count == 201) then
        return{(((-1)^num)*num)/4,-((-1)^num*num)/2}
    elseif (cache.melee_light_count == 2 or cache.melee_light_count == 200) then
        return{(((-1)^num)*num)/3.5,((-1)^num*num)/3}
    elseif (cache.melee_light_count == 3 or cache.melee_light_count == 4) then
        return{0,0}
    elseif (cache.melee_light_count == 0 or cache.melee_light_count == 100 or cache.melee_light_count == 101 or cache.melee_light_count == 102) then
        if (cache.melee_light_count == 0) then
            return{num,0}
        elseif (cache.melee_light_count == 100) then
            return{num+5,0}
        elseif (cache.melee_light_count == 101) then
            return{-num,0}
        elseif (cache.melee_light_count == 102) then
            return{-num-5,0}
        end
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
    if (current_timestamp - last_shoot_timestamp > shoot_interval + 500 or cache.melee_light_count == 100) then
        cache.melee_light_count = 0
    end
    -- 初始化射速计数器
    if (api:getShootInterval() >= 700) then
        api:adjustShootInterval(600-api:getShootInterval())
    end
    -- 初始化延迟
    local delay = 0
    -- 攻击
    if (cache.melee_light_count == 0) then
        if (api:getAimingProgress() > 0) then
            -- 大回旋起手式
            delay = 2200
            api:adjustShootInterval(150)
            api:safeAsyncTask(function ()
                    cache.melee_light_count = 101
                    api:shootOnce(false)
                    cache.melee_light_count = 0
                    api:shootOnce(false)
                    cache.melee_light_count = 2
                    return false
                end,delay,0,1)
        else
            -- 劈砍1
            delay = 1000
            api:safeAsyncTask(function ()api:shootOnce(false)return false end,delay,0,1)
            cache.melee_light_count = cache.melee_light_count + 1
        end
    elseif (cache.melee_light_count == 1) then
        if (api:getAimingProgress() > 0) then
            -- 快速双击
            api:adjustShootInterval(750)
            api:safeAsyncTask(function ()
                cache.melee_light_count = 200
                api:shootOnce(false)
                return false
            end,1000,0,1)
            api:safeAsyncTask(function ()
                cache.melee_light_count = 201
                api:shootOnce(false)
                cache.melee_light_count = 3
                return false
            end,10000,0,1)
        else
            -- 劈砍2
            delay = 1000
            api:safeAsyncTask(function ()api:shootOnce(false)return false end,delay,0,1)
            cache.melee_light_count = cache.melee_light_count + 1
        end
    elseif (cache.melee_light_count == 2) then
        -- 普通戳刺
        delay = 1000
        api:safeAsyncTask(function ()api:shootOnce(false)return false end,delay,0,1)
        cache.melee_light_count = cache.melee_light_count + 1
    elseif (cache.melee_light_count == 3) then
        -- 快速双击
        if (api:getAimingProgress() > 0) then
            api:adjustShootInterval(750)
            api:safeAsyncTask(function ()
                cache.melee_light_count = 200
                api:shootOnce(false)
                return false
            end,1000,0,1)
            api:safeAsyncTask(function ()
                cache.melee_light_count = 201
                api:shootOnce(false)
                cache.melee_light_count = 0
                return false
            end,10000,0,1)
        else
            -- 大回旋
            delay = 2200
            api:adjustShootInterval(150)
            api:safeAsyncTask(function ()
                    cache.melee_light_count = 100
                    api:shootOnce(false)
                    cache.melee_light_count = 101
                    api:shootOnce(false)
                    cache.melee_light_count = 102
                    api:shootOnce(false)
                    cache.melee_light_count = 0
                    api:shootOnce(false)
                    cache.melee_light_count = 4
                    return false
                end,delay,0,1)
        end
    elseif (cache.melee_light_count == 4) then
        -- 蓄力戳
        delay = 1000
        api:adjustShootInterval(400)
        api:safeAsyncTask(function ()
                api:shootOnce(false)
                api:shootOnce(false)
                cache.melee_light_count = 0
                return false
            end,delay,0,1)
    end
    api:cacheScriptData(cache)
end

return M