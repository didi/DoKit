//
//  BaseViewController.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import UIKit

class BaseViewController: UIViewController, BaseBigTitleViewDelegate {
    
    var bigTitleView: BaseBigTitleView?
    //功能的首页需要使用大标题，次级页面使用普通标题
    var needBigTitleView: Bool {
        return false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.white
        
        if needBigTitleView {
            bigTitleView = BaseBigTitleView(frame: CGRect(x: 0, y: 0, width: view.width, height: kSizeFrom750_Landscape(178)))
            bigTitleView!.delegate = self
            view.addSubview(bigTitleView!)
        }else{
            let image = DKImage(named: "doraemon_back")
            let leftModel = NavBarItemModel(icon: image, iconSelector: #selector(leftNavBackClick))
            self.setLeftNavBarItems(items: [leftModel])
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(needBigTitleView, animated: true)
        self.navigationController?.isNavigationBarHidden = needBigTitleView
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        let appWindow : UIWindow? = UIApplication.shared.delegate?.window ?? nil
        let keyWindow : UIWindow? = UIApplication.shared.keyWindow
        if appWindow != nil && keyWindow != nil && appWindow !== keyWindow {
            appWindow!.makeKey()
        }
    }
        
    func set(title: String) {
        if let bigTitleView = bigTitleView {
            bigTitleView.title = title
        } else {
            super.title = title
        }
    }
    
    
    func setLeftNavBarItems(items: Array<NavBarItemModel>?) {
        if let items = items {
            let barItems = self.navigationItems(items: items)
            self.navigationItem.leftBarButtonItems = barItems
        }else{
            self.navigationItem.leftBarButtonItems = nil
        }
    }
    
    func setRightNavBarItems(items: Array<NavBarItemModel>?) {
        if let items = items {
            let barItems = self.navigationItems(items: items)
            self.navigationItem.rightBarButtonItems = barItems
        }else{
            self.navigationItem.rightBarButtonItems = nil
        }
        
    }
    
    func setRightNavTitle(title: String) {
        let item = NavBarItemModel(title: title, titleColor: UIColor.blue, titleSelector: #selector(rightNavTitleClick))
        let barItems = self.navigationItems(items: [item])
        self.navigationItem.rightBarButtonItems = barItems
    }
    
    func navigationItems(items: Array<NavBarItemModel>) -> Array<UIBarButtonItem> {
        var barItems: Array<UIBarButtonItem> = [UIBarButtonItem]()
        
        for item in items {
            var barItem: UIBarButtonItem
            if item.type ==  .text{
                barItem = UIBarButtonItem(title: item.text, style: .plain, target: self, action: item.selector)
                barItem.tintColor = item.textColor
            }else if item.type == .image{
                let image = item.image?.withRenderingMode(.alwaysOriginal)
                let btn = UIButton(type: .custom)
                btn.setImage(image, for: .normal)
                btn.addTarget(self, action: item.selector!, for: .touchUpInside)
                btn.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
                btn.clipsToBounds = true
                barItem = UIBarButtonItem(customView: btn)
            }else{
                barItem = UIBarButtonItem()
            }
            barItems.append(barItem)
        }
        return barItems
    }
    
    func bigTitleCloseClick() {
        self.leftNavBackClick()
    }
    
    @objc func leftNavBackClick() {
        if self.navigationController?.viewControllers.count == 1 {
            HomeWindow.shared.hide()
        }else{
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    @objc func rightNavTitleClick() {
        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view .endEditing(true)
    }
}

enum NavBarItemType{
    case text
    case image
}

class NavBarItemModel {
    var type: NavBarItemType?
    var text: String?
    var image: UIImage?
    var textColor: UIColor?
    var selector: Selector?
    
    init(title: String?, titleColor: UIColor?, titleSelector: Selector?) {
        type = .text
        text = title
        textColor = titleColor
        selector = titleSelector
    }
    
    init(icon: UIImage?, iconSelector: Selector?) {
        type = .image
        image = icon
        selector = iconSelector
    }
}

 
