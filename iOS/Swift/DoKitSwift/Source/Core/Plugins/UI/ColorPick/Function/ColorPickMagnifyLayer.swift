//
//  ColorPickMagnifyLayer.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import UIKit

class ColorPickMagnifyLayer: CALayer {
    private struct Const {
        static let magnify: CGFloat = 150; // 放大镜尺寸
        static let rimThickness: CGFloat = 3.0; // 放大镜边缘的厚度
        static let gridNum = 15; // 放大镜网格的数量
        static let pixelSkip: CGFloat = 1; // 采集像素颜色时像素的间隔
    }
    
    /// 目标视图展示位置
    var targetPoint: CGPoint = .zero
    
    /// 获取指定点的颜色值
    var pointColorCallBack: ((CGPoint) -> UIColor) = { _ in .clear }
    
    private lazy var gridCirclePath: CGPath = {
        let path = CGMutablePath()
        path.addArc(center: .init(x: 0, y: 0),
                    radius: Const.magnify / 2 - Const.rimThickness / 2,
                    startAngle: 0,
                    endAngle: .pi * 2.0,
                    clockwise: true
        )
        return path
    }()
        
    override init() {
        super.init()
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        anchorPoint = CGPoint(x: 0.5, y: 1)
        bounds = CGRect(x: -Const.magnify / 2, y: -Const.magnify / 2, width: Const.magnify, height: Const.magnify)
        
        let magnifyLayer = CALayer()
        magnifyLayer.bounds = bounds
        magnifyLayer.position = CGPoint(x: bounds.midX, y: bounds.midY)
        magnifyLayer.magnificationFilter = .nearest
        magnifyLayer.contents = makeMagnifyImage()?.cgImage
        addSublayer(magnifyLayer)
    }
    
    override func draw(in ctx: CGContext) {
        ctx.addPath(gridCirclePath)
        ctx.clip()
        
        let gridSize: CGFloat = (Const.magnify / CGFloat(Const.gridNum)).rounded(.up)
        // 由于锚点修改，这里需要偏移
        var currentPoint = targetPoint
        currentPoint.x -= CGFloat(Const.gridNum) * Const.pixelSkip / 2
        currentPoint.y -= CGFloat(Const.gridNum) * Const.pixelSkip / 2
        // 放大镜中画出网格，并使用当前点和周围点的颜色进行填充
        for j in 0 ..< Const.gridNum {
            for i in 0 ..< Const.gridNum {
                let rect = CGRect(
                    x: gridSize * CGFloat(i) - Const.magnify / 2,
                    y: gridSize * CGFloat(j) - Const.magnify / 2,
                    width: gridSize,
                    height: gridSize
                )
                let color = pointColorCallBack(currentPoint)
                ctx.setFillColor(color.cgColor)
                ctx.fill(rect)
                // 横向寻找下一个相邻点
                currentPoint.x += Const.pixelSkip;
            }
            // 一行绘制完毕，横向回归起始点，纵向寻找下一个点
            currentPoint.x -= CGFloat(Const.gridNum) * Const.pixelSkip
            currentPoint.y += Const.pixelSkip
        }
    }
}

// MARK:- Private
extension ColorPickMagnifyLayer {
    
    private func makeMagnifyImage() -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(bounds.size, false, 0)
        guard let ctx = UIGraphicsGetCurrentContext() else {
            return nil
        }
        
        ctx.translateBy(x: Const.magnify / 2, y: Const.magnify / 2)
        // 绘制裁剪区域
        ctx.saveGState()
        ctx.addPath(gridCirclePath)
        ctx.clip()
        ctx.restoreGState()
        
        // 绘制放大镜边缘
        ctx.setLineWidth(Const.rimThickness)
        ctx.setStrokeColor(UIColor.black.cgColor)
        ctx.addPath(gridCirclePath)
        ctx.strokePath()
        
        // 绘制两条边缘线中间的内容
        ctx.setLineWidth(Const.rimThickness - 1)
        ctx.setStrokeColor(UIColor.white.cgColor)
        ctx.addPath(gridCirclePath)
        ctx.strokePath()
            
        // 绘制中心的选择区域
        let gridWidth: CGFloat = (Const.magnify/CGFloat(Const.gridNum)).rounded(.up)
        let offset = -(gridWidth + 1) / 2
        let selectedRect = CGRect(x: offset, y: offset, width: gridWidth, height: gridWidth)
        ctx.addRect(selectedRect)
        let color = UIColor.dynamic(with: .black, dark: UIColor.white)
        ctx.setStrokeColor(color.cgColor)
        ctx.setLineWidth(1.0)
        ctx.strokePath()
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
}
