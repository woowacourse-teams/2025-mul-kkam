import HealthKit

final class HealthKitManager {
    static let shared = HealthKitManager()
    
    private let healthStore = HKHealthStore()
    private let read = Set([
        HKSampleType.quantityType(forIdentifier: .activeEnergyBurned)!
    ])
    
    func requestAuthorization(completion: @escaping (Bool) -> Void) {
        healthStore.requestAuthorization(
            toShare: nil,
            read: read
        ) { success, error in
            DispatchQueue.main.async {
                if success {
                    print("HealthKit authorization success")
                } else {
                    print(
                        "HealthKit authorization failed:",
                        error?.localizedDescription ?? "unknown error"
                    )
                }
                completion(success)
            }
        }
    }
}
