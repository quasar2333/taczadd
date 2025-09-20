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
        return{ (num-5)/2 , (num-5)/1.5 }
    elseif (cache.melee_light_count == 1) then
        return{ (5-num)/1.5 , (num-5)/3 }
    elseif (cache.melee_light_count == 2 or cache.melee_light_count == 200 or cache.melee_light_count == 201 or cache.melee_light_count == 202) then
        if (cache.melee_light_count == 2) then
            return{num/1.2,0}
        elseif (cache.melee_light_count == 200) then
            return{num/1.2+8.3,0}
        elseif (cache.melee_light_count == 201) then
            return{-num/1.2,0}
        elseif (cache.melee_light_count == 202) then
            return{-num/1.2-8.3,0}
        end
    elseif (cache.melee_light_count == 100 or cache.melee_light_count == 101) then
        if (cache.melee_light_count == 100) then
            return{num/2,num/1.5}
        elseif (cache.melee_light_count == 101) then
            return{-num/2,-num/1.5}
        end
    elseif (cache.melee_light_count == 150 or cache.melee_light_count == 151) then
        if (cache.melee_light_count == 150) then
            return{num/1.5,-num/3}
        elseif (cache.melee_light_count == 151) then
            return{-num/1.5,num/3}
        end
    elseif (cache.melee_light_count == 20) then
        return{-num/1.5,num/7}
    elseif (cache.melee_light_count == 21) then
        return{num/1.5,-num/7}
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
    if (api:getShootInterval() >= 900) then
        api:adjustShootInterval(600-api:getShootInterval())
    end
    -- 初始化延迟
    local delay = 0
    -- 攻击
    if (cache.melee_light_count == 0) then
        -- 劈砍1
        delay = 1000
        api:safeAsyncTask(function ()
            if (api:getAmmoAmount() <= 1) then
                api:shootOnce(false)
            else
                cache.melee_light_count = 101
                api:shootOnce(api:isShootingNeedConsumeAmmo())
                cache.melee_light_count = 100
                api:shootOnce(false)
            end
            cache.melee_light_count = 1
            return false end,delay,0,1)
    elseif (cache.melee_light_count == 1) then
        -- 劈砍2
        delay = 1000
        api:safeAsyncTask(function ()
            if (api:getAmmoAmount() <= 1) then
                api:shootOnce(false)
            else
                cache.melee_light_count = 150
                api:shootOnce(api:isShootingNeedConsumeAmmo())
                cache.melee_light_count = 151
                api:shootOnce(false)
            end
            if (api:getFireMode() == AUTO) then
                cache.melee_light_count = 0
            else
                cache.melee_light_count = 2
            end
            return false end,delay,0,1)
    elseif (cache.melee_light_count == 2) then
        -- 横批
        delay = 1800
        api:adjustShootInterval(400)
        api:safeAsyncTask(function ()
            if (api:getAmmoAmount() <= 1) then
                api:shootOnce(false)
                cache.melee_light_count = 201
                api:shootOnce(false)
            else
                api:shootOnce(api:isShootingNeedConsumeAmmo())
                cache.melee_light_count = 200
                api:shootOnce(false)
                cache.melee_light_count = 201
                api:shootOnce(false)
                cache.melee_light_count = 202
                api:shootOnce(false)
            end
            cache.melee_light_count = 0
            return false end,delay,0,1)
    end
    api:cacheScriptData(cache)
end

function M.start_reload(api)
    local cache = {
        reloaded = 0,
        attacked = 0,
        melee_light_count = 0,
        is_tactical = 0
    }
    if (api:getAmmoAmount() > 1) then
        cache.is_tactical = 1
    end
    api:cacheScriptData(cache)
    return true
end

local function getReloadTimingFromParam(param)
    -- Need to convert time from seconds to milliseconds
    local reload_cooldown_empty = param.reload_cooldown_empty * 1000
    local reload_cooldown_tactical = param.reload_cooldown_tactical * 1000
    local reload_feed_empty = param.reload_feed_empty * 1000
    local reload_feed_tactical = param.reload_feed_tactical * 1000
    local reload_attack_empty = param.reload_attack_empty * 1000
    -- Check if any timing is nil
    if (reload_cooldown_empty == nil or reload_cooldown_tactical == nil or reload_feed_empty == nil or reload_feed_tactical == nil or reload_attack_empty == nil) then
        return nil
    end
    return reload_cooldown_empty, reload_cooldown_tactical, reload_feed_empty, reload_feed_tactical, reload_attack_empty
end

function M.tick_reload(api)
    local param = api:getScriptParams();
    local reload_cooldown_empty, reload_cooldown_tactical, reload_feed_empty, reload_feed_tactical, reload_attack_empty = getReloadTimingFromParam(param)
    local reload_time = api:getReloadTime()
    local cache = api:getCachedScriptData()

    if (cache.is_tactical == 0) then
        -- 处理空枪换弹
        if (reload_time < reload_feed_empty) then
            return EMPTY_RELOAD_FEEDING, reload_feed_empty - reload_time
        elseif (reload_time >= reload_feed_empty and reload_time <= reload_attack_empty) then
            -- 处理feed逻辑
            if (cache.reloaded == 0) then
                if (api:isShootingNeedConsumeAmmo()) then
                    api:consumeAmmoFromPlayer(1)
                end
                api:putAmmoInMagazine(api:getNeededAmmoAmount())
                cache.reloaded = 1
            end
            return EMPTY_RELOAD_FINISHING, reload_cooldown_empty - reload_time
        elseif (reload_time > reload_attack_empty and reload_time <= reload_cooldown_empty) then
            -- 处理攻击判定
            if (cache.attacked == 0) then
                cache.melee_light_count = 20
                api:shootOnce(false)
                cache.melee_light_count = 21
                api:shootOnce(false)
                cache.melee_light_count = 0
                cache.attacked = 1
            end
            return EMPTY_RELOAD_FINISHING, reload_cooldown_empty - reload_time
        elseif (reload_time > reload_cooldown_empty) then
            return NOT_RELOADING, -1
        end
    else
        -- 处理战术换弹
        if (reload_time < reload_feed_tactical) then
            return TACTICAL_RELOAD_FEEDING, reload_feed_tactical - reload_time
        elseif (reload_time >= reload_feed_tactical and reload_time <= reload_cooldown_tactical) then
            -- 处理feed逻辑
            if (cache.reloaded == 0) then
                if (api:isShootingNeedConsumeAmmo()) then
                    api:consumeAmmoFromPlayer(1)
                end
                api:putAmmoInMagazine(api:getNeededAmmoAmount())
                cache.reloaded = 1
            end
            return TACTICAL_RELOAD_FINISHING, reload_cooldown_tactical - reload_time
        elseif (reload_time > reload_cooldown_tactical) then
            return NOT_RELOADING, -1
        end
    end
    api:cacheScriptData(cache)
end

return M