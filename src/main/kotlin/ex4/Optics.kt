package ex4

import arrow.optics.optics

fun main() {
    val employee = Employee("Vlad", Company("Wolt", Address("Berlin", Street(5, "Stralauer Allee"))))

    // Mistaken in street number let's change it in immutable way

    val employeeWithCopy = employee.copy(
        company = employee.company.copy(
            address = employee.company.address.copy(
                street = employee.company.address.street.copy(
                    number = 6
                )
            )
        )
    )
//    println(employee)
//    println(employee.hashCode())
//
//    println(employeeWithCopy)
//    println(employeeWithCopy.hashCode())

    // region:Lens
//    val employeeWithLens = companyLens
//        .compose(addressLens)
//        .compose(streetLens)
//        .compose(streetNumberLens)
//        .modify(employee) { 6 }
//
//    println(employeeWithLens)
//    println(employeeWithLens.hashCode())
    // endregion:Lens

    // region:Optics
    val employeeWithNewStreetNumber = Employee.company.address.street.number.modify(employee) { 6 }
    val employeeWithNewStreet = Employee.company.address.street.set(employee,  Street(12, "Mauerstraße"))
    println(employeeWithNewStreetNumber)
    println(employeeWithNewStreet)

    val newEmployee = opticsCompose(employee,
        { Employee.company.address.street.number.modify(it) { 3 } },
        { Employee.company.address.street.name.modify(it) { "Arkadiankatu" } },
        { Employee.company.address.city.modify(it) { "Helsinki" } },
        { Employee.name.modify(it) { "Tommi" } }
    )

    println(newEmployee)
    // endregion:Optics
}

//data class Street(val number: Int, val name: String)
//data class Address(val city: String, val street: Street)
//data class Company(val name: String, val address: Address)
//data class Employee(val name: String, val company: Company)

// region:Lens
/**
 * Lens is an object made to zoom in on a specific property of another object
 */
//val companyLens: Lens<Employee, Company> = Lens(
//    get = { it.company },
//    set = { employee: Employee, company: Company -> employee.copy(company = company) }
//)
//
//val addressLens: Lens<Company, Address> = Lens(
//    get = { it.address },
//    set = { company: Company, address: Address -> company.copy(address = address) }
//)
//
//val streetLens: Lens<Address, Street> = Lens(
//    get = { it.street },
//    set = { address: Address, street: Street -> address.copy(street = street) }
//)
//
//val streetNumberLens: Lens<Street, Int> = Lens(
//    get = { it.number },
//    set = { street: Street, number: Int -> street.copy(number = number) }
//)
// endregion:Lens

// region: Optics
@optics
data class Street(val number: Int, val name: String) {
    companion object
}

@optics
data class Address(val city: String, val street: Street) {
    companion object
}

@optics
data class Company(val name: String, val address: Address) {
    companion object
}

@optics
data class Employee(val name: String, val company: Company) {
    companion object
}
// endregion: Optics

fun <T> opticsCompose(entity: T, vararg transformations: (T) -> T): T {
    return transformations.fold(entity, { e, transformation -> transformation(e) })
}