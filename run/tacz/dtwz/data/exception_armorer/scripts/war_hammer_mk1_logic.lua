local M = {}

function M.shoot(api)
    if (api:getAmmoAmount() == 1) then
        api:safeAsyncTask(function ()
            api:shootOnce(false)
            return false end,4300,0,1)
    else
        api:safeAsyncTask(function ()
            api:shootOnce(api:isShootingNeedConsumeAmmo())
            api:shootOnce(false)
            return false end,4300,0,1)
    end
end

function M.start_reload(api)
    local cache = {
        reloaded = 0
    }
    api:cacheScriptData(cache)
    return true
end

local function getReloadTimingFromParam(param)
    local reload_cooldown = param.reload_cooldown * 1000
    local reload_feed = param.reload_feed * 1000
    if (reload_cooldown == nil or reload_feed == nil) then
        return nil
    end
    return reload_cooldown, reload_feed
end

function M.tick_reload(api)
    local param = api:getScriptParams();
    local reload_cooldown, reload_feed = getReloadTimingFromParam(param)
    local reload_time = api:getReloadTime()
    local cache = api:getCachedScriptData()

    if (reload_time < reload_feed) then
        return TACTICAL_RELOAD_FEEDING, reload_feed - reload_time
    elseif (reload_time >= reload_feed and reload_time <= reload_cooldown) then
        -- 处理feed逻辑
        if (cache.reloaded == 0) then
            if (api:isShootingNeedConsumeAmmo()) then
                api:consumeAmmoFromPlayer(1)
            end
            api:putAmmoInMagazine(api:getNeededAmmoAmount())
            cache.reloaded = 1
        end
        return TACTICAL_RELOAD_FINISHING, reload_cooldown - reload_time
    elseif (reload_time > reload_cooldown) then
        return NOT_RELOADING, -1
    end
    api:cacheScriptData(cache)
end

return M