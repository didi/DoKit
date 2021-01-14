//
//  DoraemonDemoGPSViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/18.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import MapKit

class DoraemonDemoGPSViewController: DoraemonDemoBaseViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    var mapView: MKMapView!
    var lcManager: CLLocationManager?
    var annotation: DoraemonDemoGPSAnnotation?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("模拟位置")
        
        mapView = MKMapView(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: view.height))
        mapView.mapType = .standard
        mapView.delegate = self
        view.addSubview(mapView)
        
        if CLLocationManager.locationServicesEnabled() {
            lcManager = CLLocationManager()
            lcManager?.delegate = self
            lcManager?.distanceFilter = 100
            lcManager?.desiredAccuracy = kCLLocationAccuracyBest
            lcManager?.startUpdatingLocation()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let l = locations[0]
        print("location at \(l.coordinate.longitude) \(l.coordinate.latitude)")
        self.refreshAnnotation(location: l)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("location failure")
    }
    
    func refreshAnnotation(location loc:CLLocation) {
        mapView.centerCoordinate = loc.coordinate
        mapView.setRegion(MKCoordinateRegion(center: loc.coordinate, span: MKCoordinateSpan(latitudeDelta: 40, longitudeDelta: 40)), animated: true)
        
        if annotation == nil {
            annotation = DoraemonDemoGPSAnnotation(coordinate: loc.coordinate, title: "title", subtitle: "subtitle", icon: UIImage(named: "AppIcon"))
        }else{
            mapView.removeAnnotation(annotation!)
        }
        annotation?.coordinate = loc.coordinate
        mapView.addAnnotation(annotation!)
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is DoraemonDemoGPSAnnotation {
            let annotation = annotation as! DoraemonDemoGPSAnnotation
            let key = "AnnotationIdentifier"
            var view = mapView .dequeueReusableAnnotationView(withIdentifier: key)
            if view==nil {
                view = MKAnnotationView(annotation: annotation, reuseIdentifier: key)
            }
            view?.annotation = annotation
            view?.canShowCallout = false
            view?.image = annotation.icon
            return view
        }
        
        return nil
    }


}
