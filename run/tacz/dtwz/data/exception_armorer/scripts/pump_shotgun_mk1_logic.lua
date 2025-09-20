local M = {}

function M.shoot(api)
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            overload = 1,
            reloading = 0,
            feed = 0
        }
    end
    if (cache.overload == 1) then
        api:shootOnce(api:isShootingNeedConsumeAmmo())
    elseif (cache.overload == 2) then
        api:shootOnce(api:isShootingNeedConsumeAmmo())
        api:shootOnce(false)
    elseif (cache.overload == 3) then
        api:shootOnce(api:isShootingNeedConsumeAmmo())
        api:shootOnce(false)
        api:shootOnce(false)
    end
end

function M.start_reload(api)
    local cache = api:getCachedScriptData()
    if (cache == nil) then
        cache = {
            overload = 1,
            reloading = 0,
            feed = 0
        }
    end
    cache.feed = 0
    if (api:getAmmoAmount() == 0) then
        cache.overload = 0
        cache.reloading = 1
    else
        cache.reloading = 0
    end
    api:cacheScriptData(cache)
    return true
end

function M.tick_reload(api)
    local reload_time = api:getReloadTime()
    local params = api:getScriptParams()
    local overload_time = params.overload_time * 1000
    local overload_feed_time = params.overload_feed_time * 1000
    local feed_time = params.feed_time * 1000
    local total_time = params.total_time * 1000
    local cache = api:getCachedScriptData()

    if (cache.reloading == 1) then

        if (reload_time < feed_time) then
            return TACTICAL_RELOAD_FEEDING, feed_time - reload_time
        elseif (reload_time >= feed_time and reload_time <= total_time) then
            if (cache.feed == 0) then
                cache.feed = 1
                cache.overload = 1
                if (api:isShootingNeedConsumeAmmo()) then
                    api:consumeAmmoFromPlayer(1)
                    api:putAmmoInMagazine(1)
                else
                    api:putAmmoInMagazine(1)
                end
                api:cacheScriptData(cache)
            end
            return TACTICAL_RELOAD_FINISHING, total_time - reload_time
        elseif (reload_time > total_time) then
            return NOT_RELOADING, -1
        end

    else

        if (cache.overload == 3) then
            return NOT_RELOADING, -1
        end
        if (reload_time < overload_feed_time) then
            return TACTICAL_RELOAD_FEEDING, overload_feed_time - reload_time
        elseif (reload_time >= overload_feed_time and reload_time <= overload_time) then
            if (cache.feed == 0) then
                cache.feed = 1
                cache.overload = cache.overload + 1
                if (api:isShootingNeedConsumeAmmo()) then
                    api:consumeAmmoFromPlayer(1)
                end
                api:cacheScriptData(cache)
            end
            return TACTICAL_RELOAD_FINISHING, overload_time - reload_time
        elseif (reload_time > overload_time) then
            return NOT_RELOADING, -1
        end
    end

    api:cacheScriptData(cache)
    return NOT_RELOADING, -1
end

return M