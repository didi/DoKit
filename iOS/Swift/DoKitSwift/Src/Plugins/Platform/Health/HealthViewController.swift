//
//  HealthViewController.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthViewController: BaseViewController {
    
    private lazy var scrollView: UIScrollView = {
        $0.backgroundColor = .white
        $0.delegate = self
        $0.isPagingEnabled = true
        return $0
    }(UIScrollView(frame: .zero))
    
    private lazy var homeView: HealthHomeView = {
        weak var weakSelf = self
        $0.addBlock {
            let currentStatus = HealthManager.shared.start
            if currentStatus {
                weakSelf?.showEndAlert(true)
            } else {
                AlertUtil.handleAlertAction(vc: self, title: "提示", text: "是否重启App开启健康体检", ok: "确定", cancel: "取消", okBlock: {
                    HealthManager.shared.rebootAppForHealthCheck()
                }) {}
            }
        }
        return $0
    }(HealthHomeView())
    
    private lazy var instructionsView: HealthInstructionsView = {
        return $0
    }(HealthInstructionsView())
    
    private lazy var footerView: HealthFooterView = {
        $0.delegate = self
        $0.renderUIWithTitleImg(true)
        return $0
    }(HealthFooterView())
        
    override func viewDidLoad() {
        super.viewDidLoad()

        setUpUI()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        let offset_y = self.bigTitleView!.bottom
        homeView.frame = CGRect(x: 0, y: 0, width: view.width, height: view.height - offset_y)
        scrollView.frame = CGRect(x: 0, y: offset_y, width: view.width, height: view.height - offset_y)
        scrollView.contentSize = CGSize(width: homeView.width, height: homeView.height * 2)
        instructionsView.frame = CGRect(x: 0, y: homeView.bottom, width: view.width, height: view.height - offset_y)
        
        let footerHeight = kSizeFrom750_Landscape(145)
        var footerTop = self.view.height - footerHeight
        if #available(iOS 11.0, *) {
            footerTop -= self.view.safeAreaInsets.bottom
        }
        footerView.frame = CGRect(x: 0, y: footerTop, width: view.width, height: footerHeight)
    }
    
    override func needBigTitleView() -> Bool {
        return true
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

// MARK: - Else
extension HealthViewController {
    func showFooter(_ show: Bool) {
        footerView.isHidden = !show
        scrollView.isScrollEnabled = show
    }
    
    func showEndAlert(_ show: Bool) {
        guard show else {
            return
        }
        let alertView = HealthAlertView()
        alertView.block = {[weak self] (type) in
            switch type {
            case .discard: // 丢弃
                HealthManager.shared.caseName = ""
                HealthManager.shared.testPerson = ""
                self?.showFooter(true)
                self?.homeView.btnView.statusForBtn(start: false)
                self?.homeView.startingTitle.renderUIWithTitle(LocalizedString("点击开始检测"))
                HealthManager.shared.stopHealthCheck()
            case .submit: // 提交
                let result = alertView.getInputText()
                guard result.count == 2 else {
                    return
                }
                HealthManager.shared.caseName = result.first as NSString?
                HealthManager.shared.testPerson = result.last as NSString?
                self?.showFooter(true)
                self?.homeView.btnView.statusForBtn(start: false)
                self?.homeView.startingTitle.renderUIWithTitle(LocalizedString("点击开始检测"))
                HealthManager.shared.stopHealthCheck()
            default: break
            }
        }
            
        self.view.addSubview(alertView)
    }
}

// MARK: - UI
extension HealthViewController {
    func setUpUI() {
        self.setTitle(title: LocalizedString("健康体检"))
        
        scrollView.addSubview(homeView)
        scrollView.addSubview(instructionsView)
        view.addSubview(scrollView)
        view.addSubview(footerView)

        self.showFooter(HealthManager.shared.start)
    }
}
