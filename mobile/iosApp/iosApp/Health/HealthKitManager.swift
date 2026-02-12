import HealthKit

final class HealthKitManager {
    private let healthStore = HKHealthStore()
    private let read = Set([
        HKSampleType.quantityType(forIdentifier: .activeEnergyBurned)!
    ])

    func requestAuthorization() {
        healthStore.requestAuthorization(
            toShare: nil,
            read: read
        ) { success, error in
            if success {
                print("HealthKit authorization success")
            } else {
                print(
                    "HealthKit authorization failed:",
                    error?.localizedDescription ?? "unknown error"
                )
            }
        }
    }
}
