local capabilities = require "st.capabilities"
local Driver = require "st.driver"
local cosock = require "cosock"                 -- just for time
local socket = require "cosock.socket"          -- just for time
local json = require "dkjson"
local log = require "log"
local comms = require "comms"
local initialized = false
local base_url
local capability_ac_temp_set = capabilities['imageafter45121.acTempSet']
local capability_ac_auto_clean = capabilities['imageafter45121.acAutoClean']
local capability_ac_op_mode = capabilities['imageafter45121.acOpMode']
local capability_ac_wind_level = capabilities['imageafter45121.acWindLevel']
local capability_ac_direction = capabilities['imageafter45121.acDirection']
local capability_ac_co_mode = capabilities['imageafter45121.acCoMode']
local capability_ac_volume = capabilities['imageafter45121.acVolume']
local capability_ac_operation = capabilities['imageafter45121.acOperation']

-- Divoom API http://doc.divoom-gz.com/web/#/12?page_id=241
local function request(path)
  log.info("<<---- af-ha153 ---->> request : ", path)
  local status, response = comms.request('GET', base_url .. path, nil)
  --log.info("<<---- af-ha153 ---->> request, status : ", status)
  --log.info("<<---- af-ha153 ---->> request, response : ", response)
  if status then
    responseTable, pos, err = json.decode(response, 1, nil)
    if responseTable.status == 'Success' then
      return true, responseTable;
    else
      return false;
    end
  else
    return false;
  end
end

local function refresh_handler(driver, device, command)
  log.info("<<---- af-ha153 ---->> refresh_handler")
  local path = '/get/devicestate'
  local status, response = request(path);

  local attr = response.data.deviceState.device.attr;
  if status then
    for k, state in pairs(attr) do
      log.info("<<---- af-ha153 ---->> value.id  : ", state.id)
      log.info("<<---- af-ha153 ---->> value.value  : ", state.value)

      if state.id == 'AC_FUN_POWER' then
        local on_off = (state.value == 'On') and capabilities.switch.switch.on() or capabilities.switch.switch.off()
        device.profile.components['main']:emit_event(on_off)
      end

      if state.id == 'AC_FUN_TEMPNOW' then
        device.profile.components['main']:emit_event(capabilities.temperatureMeasurement.temperature({ value = tonumber(state.value), unit = "C" }))
      end

      if state.id == 'AC_FUN_TEMPSET' then
        device.profile.components['main']:emit_event(capability_ac_temp_set.acTempSet({ value = tonumber(state.value) }))
      end

      if state.id == 'AC_ADD_AUTOCLEAN' then
        local on_off = (state.value == 'On') and 'On' or 'Off'
        device.profile.components['main']:emit_event(capability_ac_auto_clean.acAutoClean({ value = on_off }))
      end

      if state.id == 'AC_ADD_VOLUME' then
        device.profile.components['main']:emit_event(capability_ac_volume.acVolume({ value = state.value }))
      end

      if state.id == 'AC_FUN_COMODE' then
        device.profile.components['main']:emit_event(capability_ac_co_mode.acCoMode({ value = state.value }))
      end

      if state.id == 'AC_FUN_DIRECTION' then
        device.profile.components['main']:emit_event(capability_ac_direction.acDirection({ value = state.value }))
      end

      if state.id == 'AC_FUN_OPERATION' then
        device.profile.components['main']:emit_event(capability_ac_operation.acOperation({ value = state.value }))
      end

      if state.id == 'AC_FUN_OPMODE' then
        device.profile.components['main']:emit_event(capability_ac_op_mode.acOpMode({ value = state.value }))
      end

      if state.id == 'AC_FUN_COMODE' then
        device.profile.components['main']:emit_event(capability_ac_co_mode.acCoMode({ value = state.value }))
      end

      if state.id == 'AC_FUN_WINDLEVEL' then
        device.profile.components['main']:emit_event(capability_ac_wind_level.acWindLevel({ value = state.value }))
      end
    end
  end
end

local function device_init(driver, device)
  log.info("<<---- af-ha153 ---->> device_init")
  initialized = true
  base_url = device.preferences.relayServerIp
  refresh_handler(driver, device, null)
end

local function device_added (driver, device)
  log.info("<<---- af-ha153 ---->> device_added")
end

local function device_doconfigure (_, device)
  -- Nothing to do here!
end

local function device_removed(_, device)
  log.info("<<---- af-ha153 ---->> device_removed : ", device.id .. ": " .. device.device_network_id)
  initialized = false
end

local function device_driver_switched(driver, device, event, args)
  log.info("<<---- af-ha153 ---->> device_driver_switched")
end

local function shutdown_handler(driver, event)
  log.info("<<---- af-ha153 ---->> shutdown_handler")
end

local function device_info_changed (driver, device, event, args)
  log.info("<<---- af-ha153 ---->> device_info_changed")
  if args.old_st_store.preferences.relayServerIp ~= device.preferences.relayServerIp then
    base_url = device.preferences.relayServerIp;
  end
end

local function discovery_handler(driver, _, should_continue)
  if not initialized then
    log.info("Creating Web Request device")
    local MFG_NAME = 'SmartThings Community'
    local VEND_LABEL = 'Samsung af-ha153'
    local MODEL = ' af-ha153'
    local ID = ' af-ha153' .. '_' .. socket.gettime()
    local PROFILE = 'LAN-af-ha153'

    local create_device_msg = {
      type = "LAN",
      device_network_id = ID,
      label = VEND_LABEL,
      profile = PROFILE,
      manufacturer = MFG_NAME,
      model = MODEL,
      vendor_provided_label = VEND_LABEL,
    }
    assert(driver:try_create_device(create_device_msg), "failed to create af-ha153 device")
    log.debug("Exiting device creation")
  else
    log.info('af-ha153 device already created')
  end
end

local switch_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  local path = (command.command == "on") and '/control/AC_FUN_POWER/On' or '/control/AC_FUN_POWER/Off'
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_temp_set_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_TEMPSET/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_auto_clean_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  local path = (command.command == "on") and '/control/AC_ADD_AUTOCLEAN/On' or '/control/AC_FUN_POWER/Off'
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_op_mode_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_OPMODE/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_wind_level_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_WINDLEVEL/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_direction_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_DIRECTION/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_co_mode_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_COMODE/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_volume_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_ADD_VOLUME/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local ac_operation_handler = function(driver, device, command)
  log.info("<<---- af-ha153 ---->> : ", command.command)
  log.info("<<---- af-ha153 ---->> : ", command.args.value)
  local path = '/control/AC_FUN_OPERATION/' .. command.args.value
  local status, response = request(path);
  refresh_handler(driver, device, command)
end

local lanDriver = Driver("lanDriver", {
  discovery = discovery_handler,
  lifecycle_handlers = {
    added = device_added,
    init = device_init,
    driverSwitched = device_driver_switched,
    infoChanged = device_info_changed,
    doConfigure = device_doconfigure,
    removed = device_removed
  },
  driver_lifecycle = shutdown_handler,
  supported_capabilities = {
    capabilities.refresh,
    capabilities.switch,
    capabilities.temperatureMeasurement
  },
  capability_handlers = {
    [capabilities.refresh.ID] = {
      [capabilities.refresh.commands.refresh.NAME] = refresh_handler,
    },
    [capabilities.switch.ID] = {
      [capabilities.switch.commands.on.NAME] = switch_handler,
      [capabilities.switch.commands.off.NAME] = switch_handler,
    },
    [capability_ac_temp_set.ID] = {
      [capability_ac_temp_set.commands.setAcTempSet.NAME] = ac_temp_set_handler,
    },
    [capability_ac_auto_clean.ID] = {
      [capability_ac_auto_clean.commands.setAcAutoClean.NAME] = ac_auto_clean_handler,
    },
    [capability_ac_op_mode.ID] = {
      [capability_ac_op_mode.commands.setAcOpMode.NAME] = ac_op_mode_handler,
    },
    [capability_ac_wind_level.ID] = {
      [capability_ac_wind_level.commands.setAcWindLevel.NAME] = ac_wind_level_handler,
    },
    [capability_ac_direction.ID] = {
      [capability_ac_direction.commands.setAcDirection.NAME] = ac_direction_handler,
    },
    [capability_ac_co_mode.ID] = {
      [capability_ac_co_mode.commands.setAcCoMode.NAME] = ac_co_mode_handler,
    },
    [capability_ac_volume.ID] = {
      [capability_ac_volume.commands.setAcVolume.NAME] = ac_volume_handler,
    },
    [capability_ac_operation.ID] = {
      [capability_ac_operation.commands.setAcOperation.NAME] = ac_operation_handler,
    },
  }
})

log.info('LAN af-ha153 Started')
lanDriver:run()