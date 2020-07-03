//
//  OscillogramView.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/30.
//

import UIKit

class OscillogramView: UIView {
    weak var delegate: OscillogramViewDelegate?
    private var timer: DispatchSourceTimer?
    private var maxValue: Double {
        return delegate?.oscillogramMaxValue ?? 100
    }
    private lazy var closeButton: UIButton = {
        let button = UIButton(type: .custom)
        button.setImage(DKImage(named: "doraemon_close_white"), for: .normal)
        button.addTarget(self, action: #selector(stop), for: .touchUpInside)
        return button
    }()
    
    private lazy var tipLabel: UILabel = {
        let label = UILabel()
        label.textColor = UIColor(0x00DFDD)
        label.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(20))
        label.textAlignment = .center
        label.frame = CGRect(x: 0, y: self.frame.height, width: 100, height: 10)
        return label
    }()
    
    private var nodes: [Double] = []
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        clearsContextBeforeDrawing = true
        backgroundColor = UIColor(0x000000, alphaValue: 0.33)
        let closeButtonWidth: CGFloat = kSizeFrom750_Landscape(80)
        addSubview(closeButton)
        addSubview(tipLabel)
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            closeButton.rightAnchor.constraint(equalTo: rightAnchor),
            closeButton.topAnchor.constraint(equalTo: topAnchor),
            closeButton.widthAnchor.constraint(equalToConstant: closeButtonWidth),
            closeButton.heightAnchor.constraint(equalToConstant: closeButtonWidth)
        ])
    }
    
    func start() {
        timer?.cancel()
        timer = DispatchSource.makeTimerSource()
        timer?.schedule(deadline: .now(), repeating: .seconds(1), leeway: .seconds(0))
        timer?.setEventHandler { [weak self] in
            DispatchQueue.main.async {
                guard let self = self else { return }
                if let value = self.delegate?.collectData() {
                    self.nodes.append(value)
                    self.setNeedsDisplay()
                }
            }
        }
        timer?.resume()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func draw(_ rect: CGRect) {
        if points().isEmpty { return }
        let path = UIBezierPath()
        path.lineWidth = 1
        var p = points()
        let startPoint = p.removeFirst()
        path.move(to: startPoint)
        UIColor(0x00DFDD).set()
        p.forEach { (point) in
            path.addLine(to: point)
            path.addArc(withCenter: point, radius: 1, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        }
        path.stroke()
        tipLabel.text = String(format: "%.2f", p.last?.y ?? 0)
        tipLabel.center = CGPoint(x: p.last?.x ?? 0, y: (p.last?.y ?? 0) - tipLabel.height)
        tipLabel.sizeToFit()
    }
    
    private func points() -> [CGPoint] {
        let padding = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
        let spacing: CGFloat = 10
        
        let lineChartWidth = kScreenWidth - padding.left - padding.right
        let lineChartHeight = bounds.size.height - padding.top - padding.bottom
        let count = Int(floor(lineChartWidth / spacing))
        let array =  Array(nodes.suffix(count))
        var points = [CGPoint]()
        var x: CGFloat = padding.left
        array.forEach { (value) in
            let y = lineChartHeight * CGFloat(1 - Double(value) / maxValue) + padding.bottom
            points.append(CGPoint(x: x, y: y))
            x += spacing
        }
        return points
    }
    
    @objc func stop() {
        timer?.cancel()
        timer = nil
        nodes = []
        delegate?.oscillogramViewDidColsed()
    }
    
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        
        guard isUserInteractionEnabled else { return nil }
        
        guard !isHidden else { return nil }
        
        guard alpha >= 0.01 else { return nil }
        
        guard self.point(inside: point, with: event) else { return nil }
        
        for subview in subviews.reversed() {
            let convertedPoint = subview.convert(point, from: self)
            if let candidate = subview.hitTest(convertedPoint, with: event) , candidate is UIButton {
                return candidate
            }
        }
        return nil
    }

}

protocol OscillogramViewDelegate: class {
    var oscillogramMaxValue: Double {get}
    func collectData() -> Double
    func oscillogramViewDidColsed()
}
