//
//  DoraemonDemoGPSAnnotation.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/18.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import MapKit

class DoraemonDemoGPSAnnotation: NSObject,MKAnnotation {
    var coordinate: CLLocationCoordinate2D
    var title: String?
    var subtitle: String?
    var icon: UIImage?
    
    init(coordinate: CLLocationCoordinate2D, title: String?, subtitle: String?, icon: UIImage?) {
        self.coordinate = coordinate
        self.title = title
        self.subtitle = title
        self.icon = icon
    }
}
