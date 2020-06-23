//
//  HealthViewController.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthViewController: BaseViewController {
    var scrollView: UIScrollView!
    var homeView: HealthHomeView!
    var instructionsView = HealthInstructionsView()
    var footerView = HealthFooterView()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.setTitle(title: LocalizedString("健康体检"))
        
        let offset_y = self.bigTitleView!.bottom
        homeView = HealthHomeView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: view.height - offset_y))
        
        instructionsView = HealthInstructionsView(frame: CGRect(x: 0, y: homeView.bottom, width: view.bounds.width, height: view.height - offset_y))
        
        
        scrollView = UIScrollView(frame: CGRect(x: 0, y: offset_y, width: view.width, height: view.height - offset_y))
        scrollView.backgroundColor = .white
        scrollView.delegate = self
        scrollView.contentSize = CGSize(width: homeView.width, height: homeView.height * 2)//设置大小
        scrollView.isPagingEnabled = true
        
        let footerHeight = kSizeFrom750_Landscape(145)
        var footerTop = self.view.height - footerHeight

        if #available(iOS 11.0, *) {
            footerTop -= self.view.safeAreaInsets.bottom
        }
        
        footerView = HealthFooterView(frame: CGRect(x: 0, y: footerTop, width: view.width, height: footerHeight))
        footerView.delegate = self
        footerView.renderUIWithTitleImg(true)
        
        weak var weakSelf = self
        homeView.addBlock {
            let currentStatus = HealthManager.shared.start
            if currentStatus {
                weakSelf?.showEndAlert(true)
            } else {
                AlertUtil.handleAlertAction(vc: self, text: "是否重启App开启健康体检") {
                    HealthManager.shared.rebootAppForHealthCheck()
                }
            }
        }
        
        scrollView.addSubview(homeView)
        scrollView.addSubview(instructionsView)
        view.addSubview(scrollView)
        view.addSubview(footerView)

        self.showFooter(HealthManager.shared.start)
    }
    
    override func needBigTitleView() -> Bool {
        return true
    }
    
    func showFooter(_ show: Bool) {
        footerView.isHidden = !show
        scrollView.isScrollEnabled = show
    }
    
    func showEndAlert(_ show: Bool) {
        guard show else {
            return
        }
//        weak var weakSelf = self
//        let alertView = HealthAlertView()
    }
}

// MARK: - UIScrollViewDelegate
extension HealthViewController: UIScrollViewDelegate {
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let scrollToScrollStop = !scrollView.isTracking && !scrollView.isDragging && !scrollView.isDecelerating
        if (scrollToScrollStop) {
            footerView.renderUIWithTitleImg(scrollView.contentOffset.y <= 0)
            footerView.isHidden = false
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        footerView.isHidden = true
    }
}

// MARK: - HealthFooterButtonDelegate
extension HealthViewController: HealthFooterButtonDelegate {
    func footerBtnClick(sender: UIView) {
        if footerView._top {
            scrollView.contentOffset = CGPoint(x: 0, y: scrollView.height)
        } else {
            scrollView.contentOffset = CGPoint.zero
        }
        
        footerView.renderUIWithTitleImg(!footerView._top)
        footerView.isHidden = false
    }
}
