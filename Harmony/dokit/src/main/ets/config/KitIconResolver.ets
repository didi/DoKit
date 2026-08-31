/**
 * 内置工具图标映射，innerKitId 与 Android 各 Kit.icon 保持一致
 */
import { KitItem } from '../model/KitModels';

export function resolveKitIcon(item: KitItem): Resource {
  if (item.iconResource !== undefined) {
    return item.iconResource;
  }
  return resolveKitIconById(item.innerKitId);
}

export function resolveKitIconById(innerKitId: string): Resource {
  switch (innerKitId) {
    case 'dokit_sdk_platform_ck_mock':
      return $r('app.media.dk_net_mock');
    case 'dokit_sdk_platform_ck_health':
      return $r('app.media.dk_health');
    case 'dokit_sdk_platform_ck_filetransfer':
      return $r('app.media.dk_icon_file_manager');
    case 'dokit_sdk_platform_ck_mc_test':
    case 'dokit_sdk_platform_ck_mc':
      return $r('app.media.dk_icon_mc');
    case 'dokit_sdk_platform_ck_dokit_connect':
    case 'dokit_sdk_platform_ck_dokit_for_web':
      return $r('app.media.dk_dokit_for_web');
    case 'dokit_sdk_platform_ck_autotest':
      return $r('app.media.dk_icon_autotest');
    case 'dokit_sdk_platform_thread_hook':
      return $r('app.media.dk_ram');
    case 'dokit_sdk_comm_ck_appinfo':
    case 'dokit_sdk_weex_ck_info':
      return $r('app.media.dk_sys_info');
    case 'dokit_sdk_comm_ck_thirdlibinfo':
      return $r('app.media.dk_icon_third_lib');
    case 'dokit_sdk_comm_ck_devpage':
      return $r('app.media.dk_kit_devlop');
    case 'dokit_sdk_comm_ck_local_lang':
      return $r('app.media.dk_kit_local_lang');
    case 'dokit_sdk_comm_ck_sandbox':
    case 'dokit_sdk_weex_ck_storage':
      return $r('app.media.dk_file_explorer');
    case 'dokit_sdk_comm_ck_h5':
      return $r('app.media.dk_web_door');
    case 'dokit_sdk_comm_ck_cache':
      return $r('app.media.dk_data_clean');
    case 'dokit_sdk_comm_ck_log':
    case 'dokit_sdk_weex_ck_log':
      return $r('app.media.dk_log_info');
    case 'dokit_sdk_comm_ck_h5kit':
      return $r('app.media.dk_icon_h5help');
    case 'dokit_sdk_comm_ck_gps':
      return $r('app.media.dk_gps_mock');
    case 'dokit_sdk_lbs_ck_pos_adjust':
      return $r('app.media.dk_mock_location');
    case 'dokit_sdk_lbs_ck_nav':
      return $r('app.media.dk_mock_location_route');
    case 'dokit_sdk_weex_ck_devtool':
      return $r('app.media.dk_custom');
    case 'dokit_sdk_performance_ck_fps':
      return $r('app.media.dk_frame_hist');
    case 'dokit_sdk_performance_ck_cpu':
      return $r('app.media.dk_cpu');
    case 'dokit_sdk_performance_ck_arm':
      return $r('app.media.dk_ram');
    case 'dokit_sdk_performance_ck_network':
      return $r('app.media.dk_net_monitor');
    case 'dokit_sdk_comm_ck_crash':
      return $r('app.media.dk_crash_catch');
    case 'dokit_sdk_performance_ck_block':
      return $r('app.media.dk_block_monitor');
    case 'dokit_sdk_performance_ck_img':
      return $r('app.media.dk_performance_large_picture');
    case 'dokit_sdk_comm_ck_weaknetwork':
      return $r('app.media.dk_weak_network');
    case 'dokit_sdk_performance_ck_open_coast':
      return $r('app.media.dk_time_counter');
    case 'dokit_sdk_performance_ck_hierarchy':
      return $r('app.media.dk_ui_performance');
    case 'dokit_sdk_performance_ck_method_coast':
      return $r('app.media.dk_method_cost');
    case 'dokit_sdk_performance_ck_leak':
      return $r('app.media.leak_canary_icon');
    case 'dokit_sdk_ui_ck_color_pick':
      return $r('app.media.dk_color_picker');
    case 'dokit_sdk_ui_ck_aligin_scaleplate':
      return $r('app.media.dk_align_ruler');
    case 'dokit_sdk_ui_ck_widget':
      return $r('app.media.dk_view_check');
    case 'dokit_sdk_ui_ck_border':
      return $r('app.media.dk_view_border');
    default:
      return $r('app.media.dk_custom');
  }
}
