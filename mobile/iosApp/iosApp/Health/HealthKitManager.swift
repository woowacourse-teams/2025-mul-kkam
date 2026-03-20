import HealthKit
import UIKit

final class HealthKitManager {
    static let shared = HealthKitManager()
    static let exerciseDetectionThreshold: Double = 100.0

    private init() {}

    private let activeEnergyType =
        HKObjectType.quantityType(forIdentifier: .activeEnergyBurned)!
    private let healthStore = HKHealthStore()
    private let read = Set([
        HKSampleType.quantityType(forIdentifier: .activeEnergyBurned)!
    ])

    private var onBurnedCalorieUpdated: ((Double) -> Void)?
    private var lastFiredDate: Date? {
        get { UserDefaults.standard.object(forKey: "lastFiredDate") as? Date }
        set { UserDefaults.standard.set(newValue, forKey: "lastFiredDate") }
    }

    func requestAuthorization(
        onBurnedCalorieUpdated: @escaping (Double) -> Void,
        completion: @escaping (Bool) -> Void
    ) {
        self.onBurnedCalorieUpdated = onBurnedCalorieUpdated

        healthStore.requestAuthorization(
            toShare: nil,
            read: read
        ) { success, error in
            DispatchQueue.main.async {
                if success {
                    self.enableBackgroundDelivery()
                    self.startObserverQuery()
                } else {
                    if let url = URL(string: UIApplication.openSettingsURLString) {
                        UIApplication.shared.open(url)
                    }
                }
                completion(success)
            }
        }
    }

    private func enableBackgroundDelivery() {
        healthStore.enableBackgroundDelivery(
            for: activeEnergyType,
            frequency: .hourly,
            withCompletion: { _, _ in }
        )
    }

    private func startObserverQuery() {
        let query = HKObserverQuery(
            sampleType: activeEnergyType,
            predicate: nil
        ) { [weak self] _, completionHandler, error in
            if error != nil {
                completionHandler()
                return
            }

            self?.fetchBurnedCalories {
                completionHandler()
            }
        }

        healthStore.execute(query)
    }

    private func fetchBurnedCalories(completion: @escaping () -> Void) {
        let now = Date()
        let startOfDay = Calendar.current.startOfDay(for: now)

        let predicate = HKQuery.predicateForSamples(
            withStart: startOfDay,
            end: now,
            options: .strictStartDate
        )

        let query = HKStatisticsQuery(
            quantityType: activeEnergyType,
            quantitySamplePredicate: predicate,
            options: .cumulativeSum
        ) { [weak self] _, result, error in
            if error != nil {
                completion()
                return
            }
            
            let kcal = result?.sumQuantity()?.doubleValue(for: .kilocalorie()) ?? 0

            guard let self else {
                completion()
                return
            }

            if kcal >= HealthKitManager.exerciseDetectionThreshold {
                let now = Date()
                let twoHoursAgo = now.addingTimeInterval(-2 * 60 * 60)

                if lastFiredDate == nil || lastFiredDate! < twoHoursAgo {
                    lastFiredDate = now
                    onBurnedCalorieUpdated?(kcal)
                }
            }
            
            completion()
        }

        healthStore.execute(query)
    }
}
