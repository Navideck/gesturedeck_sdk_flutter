//
//  UniHaptics.swift
//  UniHaptics
//
//  Created by Foti Dim on 14.12.19.
//  Copyright © 2019 navideck. All rights reserved.
//

import UIKit
import CoreHaptics
import AudioToolbox

public enum Style: CaseIterable {
    case selection
    case impact
    case notification
    case custom //Uses CoreHaptics API
}

public struct UniHaptic: Vibrating {

    private var engine: Vibrating

    public init(style: Style = .selection) {
        engine = {
            if #available(iOS 13.0, *), style == .custom {
                return CoreHapticsWrapper()
            }
            else if #available(iOS 10.0, *) {
                return FeedbackGeneratorWrapper(style: style)
            } else {
                return AudioToolboxWrapper()
            }
        }()
    }

    public func vibrate(intensity: Float = 0.7, sharpness: Float = 0.7) {
        engine.vibrate(intensity: intensity, sharpness: sharpness)
    }

}

protocol Vibrating {
    func vibrate(intensity: Float, sharpness: Float)
}

@available(iOS 13.0,*)
struct CoreHapticsWrapper: Vibrating {
    private var hapticEngine: CHHapticEngine?

    init() {
        hapticEngine = try? CHHapticEngine()
        hapticEngine?.resetHandler = resetHandler
        hapticEngine?.stoppedHandler = restartHandler
        hapticEngine?.playsHapticsOnly = true
        try? start()
    }

    func vibrate(intensity: Float, sharpness: Float) {
        let event = CHHapticEvent(
            eventType: .hapticTransient,
            parameters: [
                CHHapticEventParameter(parameterID: .hapticIntensity, value: intensity),
                CHHapticEventParameter(parameterID: .hapticSharpness, value: sharpness)
            ],
            relativeTime: 0)
        guard let pattern = try? CHHapticPattern(events: [event], parameters: []) else { return }
        let player = try? hapticEngine?.makePlayer(with: pattern)
        try? player?.start(atTime: CHHapticTimeImmediate)
    }

    public func start() throws {
        try hapticEngine?.start()
    }

    private func resetHandler() {
        try? hapticEngine?.start()
    }

    private func restartHandler(_ reasonForStopping: CHHapticEngine.StoppedReason? = nil) {
        resetHandler()
    }
}

@available(iOS 10.0,*)
struct FeedbackGeneratorWrapper: Vibrating {
    var feedbackGenerator: UIFeedbackGenerator
    var style: Style

    init(style: Style) {
        func makeFeedbackGenerator(of style: Style) -> UIFeedbackGenerator {
            switch style {
            case .selection:
                return UISelectionFeedbackGenerator()
            case .notification:
                return UINotificationFeedbackGenerator()
            default:
                return UIImpactFeedbackGenerator(style: .medium)
            }
        }

        self.style = style
        feedbackGenerator = makeFeedbackGenerator(of: style)
        feedbackGenerator.prepare()
    }

    func vibrate(intensity: Float, sharpness: Float) {
        defer {
            feedbackGenerator.prepare()
        }

        switch style {
        case .selection:
            (feedbackGenerator as? UISelectionFeedbackGenerator)?.selectionChanged()
        case .notification:
            switch intensity {
            case -Float.infinity..<0.4:
                (feedbackGenerator as? UINotificationFeedbackGenerator)?.notificationOccurred(.success)
            case 0.4..<0.6:
                (feedbackGenerator as? UINotificationFeedbackGenerator)?.notificationOccurred(.warning)
            default:
                (feedbackGenerator as? UINotificationFeedbackGenerator)?.notificationOccurred(.error)
            }
        default:
            switch (intensity, sharpness) {
            case (-Float.infinity...0, _):
                if #available(iOS 13.0, *) {
                    UIImpactFeedbackGenerator(style: .soft).impactOccurred()
                } else {
                    fallthrough
                }
            case (0..<0.4, _):
                UIImpactFeedbackGenerator(style: .light).impactOccurred()
            case (0.4..<0.6, _):
                UIImpactFeedbackGenerator(style: .medium).impactOccurred()
            case (1.0...Float.infinity, _):
                if #available(iOS 13.0, *) {
                    UIImpactFeedbackGenerator(style: .rigid).impactOccurred()
                } else {
                    fallthrough
                }
            case (0.6...1.0, _):
                UIImpactFeedbackGenerator(style: .heavy).impactOccurred()
            default:
                (feedbackGenerator as? UIImpactFeedbackGenerator)?.impactOccurred()
            }
        }
    }
}

struct AudioToolboxWrapper: Vibrating {
    func vibrate(intensity: Float, sharpness: Float) {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }
}
