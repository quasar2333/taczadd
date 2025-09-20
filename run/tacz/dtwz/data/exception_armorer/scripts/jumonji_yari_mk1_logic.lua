local M = {}

function M.calcSpread(api, num, spread)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_heavy_count = 0,
            spread = 0
        }
    end
    -- 根据计数器返回弹道形状
    -- 0 任意戳刺
    -- 1和100 重击第一判
    -- 2和200 重击第三判
    if (cache.spread == 0) then
    elseif (cache.spread == 1) then
        return{-num/25,num/1.2}
    elseif (cache.spread == 100) then
        return{num/25,-num/1.2}
    elseif (cache.spread == 2) then
        return{num/1.2,-num/25}
    elseif (cache.spread == 200) then
        return{-num/1.2,num/25}
    end
end

function M.shoot(api)
    -- 初始化计数器
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            melee_light_count = 0,
            melee_heavy_count = 0,
            spread = 0
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
    -- 初始化射速计数器
    if (api:getShootInterval() > 510 or api:getShootInterval() < 490) then
        api:adjustShootInterval(500-api:getShootInterval())
    end
    -- 初始化延迟
    local delay = 0
    -- 攻击
    if (api:getAimingProgress() > 0) then
        -- 重击1
        if (cache.melee_heavy_count == 0) then
            api:adjustShootInterval(300)
            api:safeAsyncTask(function ()
                cache.spread = 0
                api:shootOnce(false)
                return false
            end,700,0,1)
            api:safeAsyncTask(function ()
                cache.spread = 0
                api:shootOnce(false)
                cache.melee_heavy_count = 1
                return false
            end,4000,0,1)
        end
        -- 重击2
        if (cache.melee_heavy_count == 1) then
            api:safeAsyncTask(function ()
                -- 上挑
                cache.spread = 1
                api:shootOnce(false)
                cache.spread = 100
                api:shootOnce(false)
                api:safeAsyncTask(function ()
                    -- 重戳
                    cache.spread = 0
                    api:shootOnce(false)
                    api:safeAsyncTask(function ()
                        -- 横扫
                        cache.spread = 2
                        api:shootOnce(false)
                        cache.spread = 200
                        api:shootOnce(false)
                        api:safeAsyncTask(function ()
                            -- 重戳
                            cache.spread = 0
                            api:shootOnce(false)
                            api:shootOnce(false)
                            api:adjustShootInterval(2000)
                            return false
                        end,8000,0,1)
                        return false
                    end,7000,0,1)
                    return false
                end,8000,0,1)
                return false
            end,600,0,1)
            cache.melee_heavy_count = 0
        end
    else
        -- 轻击
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            return false
        end,600,0,1)
        -- cache.melee_heavy_count = 0
    end
    api:cacheScriptData(cache)
end

return M