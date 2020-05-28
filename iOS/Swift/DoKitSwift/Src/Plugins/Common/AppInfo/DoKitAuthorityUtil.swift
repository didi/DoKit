//
//  DoKitAuthorityUtil.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/28.
//

import Foundation
import CoreLocation
import AVFoundation
import Photos
import Contacts
import AddressBook
import EventKit

class DoKitAuthorityUtil {
    static func locationAuthority() -> String {
        var authority = "NoEnabled";
        if (CLLocationManager.locationServicesEnabled()) {
            let state = CLLocationManager.authorizationStatus();
            switch state {
            case .notDetermined:
                authority = "NotDetermined"
            case .restricted:
                authority = "Restricted"
            case .denied:
                authority = "Denied"
            case .authorizedAlways:
                authority = "Always"
            case .authorizedWhenInUse:
                authority = "WhenInUse"
            default:
                authority = ""
            }
        }
        return authority;
    }

    static func pushAuthority() -> String {
        if (UIApplication.shared.currentUserNotificationSettings?.types == .none) {
            return "NO";
        }
        return "YES";
    }

    static func cameraAuthority() -> String {
        var authority = "";
        let authStatus = AVCaptureDevice.authorizationStatus(for: AVMediaType.video);//读取设备授权状态
        switch (authStatus) {
        case .notDetermined:
                authority = "NotDetermined"
                break;
        case .restricted:
                authority = "Restricted"
                break;
        case .denied:
                authority = "Denied"
                break;
        case .authorized:
                authority = "Authorized"
                break;
            default:
                break;
        }
        return authority;
    }

    static func audioAuthority() -> String {
        var authority = ""
        let authStatus = AVCaptureDevice.authorizationStatus(for: AVMediaType.audio);//读取设备授权状态
        switch (authStatus) {
        case .notDetermined:
                authority = "NotDetermined"
                break;
            case .restricted:
                authority = "Restricted"
                break;
            case .denied:
                authority = "Denied"
                break;
            case .authorized:
                authority = "Authorized"
                break;
            default:
                break;
        }
        return authority;
    }

    static func photoAuthority() -> String {
        var authority = ""
        let current = PHPhotoLibrary.authorizationStatus();
        switch (current) {
        case .notDetermined:    //用户还没有选择(第一次)
                authority = "NotDetermined"
        case .restricted:       //家长控制
                authority = "Restricted"
        case .denied:           //用户拒绝
                authority = "Denied"
        case .authorized:       //已授权
                authority = "Authorized"
            default:
                break;
        }
        return authority;
    }

    static func addressAuthority() -> String {
        var authority = ""
        if #available(iOS 9.0, *) {//iOS9.0之后
            let authStatus = CNContactStore.authorizationStatus(for: .contacts);
            switch (authStatus) {
            case .authorized:
                    authority = "Authorized"
            case .denied:
                    authority = "Denied"
            case .notDetermined:
                    authority = "NotDetermined"
            case .restricted:
                    authority = "Restricted";
            default:
                break
            }
        }else{//iOS9.0之前
            let authorStatus = ABAddressBookGetAuthorizationStatus();
            switch (authorStatus) {
            case .authorized:
                    authority = "Authorized"
            case .denied:
                    authority = "Denied"
            case .notDetermined:
                    authority = "NotDetermined"
            case .restricted:
                    authority = "Restricted"
                default:
                    break;
            }
        }
        return authority;
    }

    static func calendarAuthority() -> String {
        var authority = ""
        let status = EKEventStore.authorizationStatus(for: EKEntityType.event);
        switch (status) {
        case .notDetermined:
                authority = "NotDetermined"
        case .restricted:
                authority = "Restricted"
        case .denied:
                authority = "Denied"
        case .authorized:
                authority = "Authorized"
        default:
                break;
        }
        return authority;
    }

    static func remindAuthority() -> String {
        var authority = ""
        let status = EKEventStore.authorizationStatus(for: EKEntityType.reminder)
        switch (status) {
        case .notDetermined:
                authority = "NotDetermined"
        case .restricted:
                authority = "Restricted"
        case .denied:
                authority = "Denied"
        case .authorized:
                authority = "Authorized"
            default:
                break;
        }
        return authority;
    }
}
