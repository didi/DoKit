//
//  DoKitHomeViewController.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import UIKit

class DoKitHomeViewController: DoKitBaseViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    var pluginArray: Array<Dictionary<String, Any>> = DoKit.shared.pluginArray
    var collectionView: UICollectionView!
    let DoKitHomeCellID = "DoKitHomeCellID"
    let DoKitHomeCloseCellID = "DoKitHomeCloseCellID"
    let DoKitHomeHeadCellID = "DoKitHomeHeadCellID"
    let DoKitHomeFootCellID = "DoKitHomeFootCellID"
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "DoKit"
        
        let fl = UICollectionViewFlowLayout()
        collectionView = UICollectionView(frame: view.bounds, collectionViewLayout: fl)
        collectionView.showsVerticalScrollIndicator = false
        collectionView.backgroundColor = UIColor.white
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(DoKitHomeCell.self, forCellWithReuseIdentifier: DoKitHomeCellID)
        collectionView.register(DoKitHomeCloseCell.self, forCellWithReuseIdentifier: DoKitHomeCloseCellID)
        collectionView.register(DoKitHomeHeadView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: DoKitHomeHeadCellID)
        collectionView.register(DoKitHomeFooterView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionFooter, withReuseIdentifier: DoKitHomeFootCellID)
        
        view.addSubview(collectionView)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return pluginArray.count+1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if section < pluginArray.count {
            let moduleDic: Dictionary<String, Any> = pluginArray[section]
            let plugins: Array<DoKitPluginModel> = moduleDic["pluginArray"] as! Array<DoKitPluginModel>
            return plugins.count
        }else{
            return 1
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let row = indexPath.row
        let section = indexPath.section
        if section < pluginArray.count {
            let homeCell: DoKitHomeCell = collectionView.dequeueReusableCell(withReuseIdentifier: DoKitHomeCellID, for: indexPath) as! DoKitHomeCell
            let moduleDic: Dictionary<String, Any> = pluginArray[section]
            let plugins: Array<DoKitPluginModel> = moduleDic["pluginArray"] as! Array<DoKitPluginModel>
            let pluginModel: DoKitPluginModel = plugins[row]
            homeCell.update(name: pluginModel.title, icon: pluginModel.icon)
            return homeCell
        }else{
            let closeCell: DoKitHomeCloseCell = collectionView.dequeueReusableCell(withReuseIdentifier: DoKitHomeCloseCellID, for: indexPath) as! DoKitHomeCloseCell
            return closeCell
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        var view = UICollectionReusableView()
        if kind == UICollectionView.elementKindSectionHeader {
            let headView: DoKitHomeHeadView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: DoKitHomeHeadCellID, for: indexPath) as! DoKitHomeHeadView
            let section = indexPath.section
            if section < pluginArray.count {
                let moduleDic: Dictionary<String, Any> = pluginArray[section]
                let title = moduleDic["module"]
                headView.renderUI(title: title as! String)
            }
            view = headView
            
        }else if kind == UICollectionView.elementKindSectionFooter {
            let footerView: DoKitHomeFooterView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: DoKitHomeFootCellID, for: indexPath) as! DoKitHomeFooterView
            let section = indexPath.section
            if  section >= pluginArray.count {
                footerView.titleLabel.text = "\(DoKitLocalizedString("当前版本")): \(DoKitVersion)"
                footerView.titleLabel.textColor = UIColor.hexColor(0x999999)
                footerView.titleLabel.textAlignment = .center
                footerView.titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
            }else{
                footerView.titleLabel.text = nil
            }
            footerView.backgroundColor = UIColor.hexColor(0xF4F5F6)
            view = footerView
            
        }
        return view
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if indexPath.section < pluginArray.count {
            return CGSize(width: kSizeFrom750_Landscape(160), height: kSizeFrom750_Landscape(128))
        }else{
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(100))
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        if section < pluginArray.count {
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(88))
        }else{
            return CGSize(width: kScreenWidth, height: 0)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForFooterInSection section: Int) -> CGSize {
        if section < pluginArray.count {
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(24))
        }else{
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(80))
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        if section < pluginArray.count {
            return UIEdgeInsets(top: 0, left: kSizeFrom750_Landscape(24), bottom: kSizeFrom750_Landscape(24), right: kSizeFrom750_Landscape(24))
        }else{
            return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let section = indexPath.section
        let row = indexPath.row
        if section < pluginArray.count {
            let moduleDic: Dictionary<String, Any> = pluginArray[section]
            let plugins: Array<DoKitPluginModel> = moduleDic["pluginArray"] as! Array<DoKitPluginModel>
            let pluginModel: DoKitPluginModel = plugins[row]
            let plugin = pluginModel.plugin
            if let plugin = plugin {
                guard let pluginClass = getClass(className: plugin) as? DoKitBasePlugin.Type else {
                    print("pluginClass not found")
                    return
                }
                let pluginInstance = pluginClass.init()
                pluginInstance.pluginDidLoad()
            }
        }
    }
    
    // swift 类名有需要添加前缀
    func getClass(className: String) -> AnyClass? {
        guard let prefix = NSStringFromClass(type(of: self)).split(separator: ".").first else {
            return nil
        }
        return NSClassFromString(prefix + ".\(className)")
    }
    
}
