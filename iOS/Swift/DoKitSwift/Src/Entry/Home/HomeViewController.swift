//
//  HomeViewController.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import UIKit

class HomeViewController: BaseViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    var collectionView: UICollectionView!
    let HomeCellID = "DoKitHomeCellID"
    let HomeCloseCellID = "DoKitHomeCloseCellID"
    let HomeHeadCellID = "DoKitHomeHeadCellID"
    let HomeFootCellID = "DoKitHomeFootCellID"
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: "DoKit")
        self.setLeftNavBarItems(items: nil)
        
        let fl = UICollectionViewFlowLayout()
        collectionView = UICollectionView(frame: view.bounds, collectionViewLayout: fl)
        collectionView.showsVerticalScrollIndicator = false
        collectionView.backgroundColor = UIColor.white
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(HomeCell.self, forCellWithReuseIdentifier: HomeCellID)
        collectionView.register(HomeCloseCell.self, forCellWithReuseIdentifier: HomeCloseCellID)
        collectionView.register(HomeHeadView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: HomeHeadCellID)
        collectionView.register(HomeFooterView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionFooter, withReuseIdentifier: HomeFootCellID)
        
        view.addSubview(collectionView)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return DoKit.shared.modules.count + 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if section < DoKit.shared.modules.count {
            return DoKit.shared.pluginMap[DoKit.shared.modules[section]]?.count ?? 0
        }
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let row = indexPath.row
        let section = indexPath.section
        if section < DoKit.shared.modules.count {
            let homeCell: HomeCell = collectionView.dequeueReusableCell(withReuseIdentifier: HomeCellID, for: indexPath) as! HomeCell
            let plugin = DoKit.shared.pluginMap[DoKit.shared.modules[section]]?[row]
            homeCell.update(name: plugin?.title, icon: plugin?.icon)
            return homeCell
        }else{
            let closeCell: HomeCloseCell = collectionView.dequeueReusableCell(withReuseIdentifier: HomeCloseCellID, for: indexPath) as! HomeCloseCell
            return closeCell
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        var view = UICollectionReusableView()
        if kind == UICollectionView.elementKindSectionHeader {
            let headView: HomeHeadView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: HomeHeadCellID, for: indexPath) as! HomeHeadView
            let section = indexPath.section
            if section < DoKit.shared.modules.count {
                headView.renderUI(title: DoKit.shared.modules[section])
            }
            view = headView
            
        }else if kind == UICollectionView.elementKindSectionFooter {
            let footerView: HomeFooterView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: HomeFootCellID, for: indexPath) as! HomeFooterView
            let section = indexPath.section
            if  section >= DoKit.shared.modules.count {
                footerView.titleLabel.text = "\(LocalizedString("当前版本")): \(DoKitVersion)"
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
        if indexPath.section < DoKit.shared.modules.count {
            return CGSize(width: kSizeFrom750_Landscape(160), height: kSizeFrom750_Landscape(128))
        }else{
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(100))
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        if section < DoKit.shared.modules.count {
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(88))
        }else{
            return CGSize(width: kScreenWidth, height: 0)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForFooterInSection section: Int) -> CGSize {
        if section < DoKit.shared.modules.count {
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(24))
        }else{
            return CGSize(width: kScreenWidth, height: kSizeFrom750_Landscape(80))
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        if section < DoKit.shared.modules.count {
            return UIEdgeInsets(top: 0, left: kSizeFrom750_Landscape(24), bottom: kSizeFrom750_Landscape(24), right: kSizeFrom750_Landscape(24))
        }else{
            return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let section = indexPath.section
        let row = indexPath.row
        if section < DoKit.shared.modules.count {
            let plugin = DoKit.shared.pluginMap[DoKit.shared.modules[section]]?[row]
            plugin?.onSelected()
        }
    }
}
