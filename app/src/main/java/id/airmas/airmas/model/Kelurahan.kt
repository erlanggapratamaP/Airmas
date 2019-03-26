package id.airmas.airmas.model

class Kelurahan{
    var id: Long? = null
    var nama: String? = null

    constructor() {}
    constructor(id: Long, nama: String) {
        this.id = id
        this.nama = nama

    }
}
