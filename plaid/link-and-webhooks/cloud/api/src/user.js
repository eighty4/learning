const {createToken} = require('./jwt')

const SAVE_USER = `
    insert into eighty4_bank.users (email)
    values ($1)
        on conflict on constraint users_email_key
        do update set last_login_when = current_timestamp
    returning user_id
`

class UserApi {
    db

    constructor(db) {
        this.db = db
    }

    async emailLogin({email}) {
        if (!email) throw new Error('emailLogin() missing email')

        const result = await this.db.query(SAVE_USER, [email])
        const userId = result.rows[0].user_id
        const token = await createToken({userId, email})
        return {userId, email, token}
    }
}

module.exports = {
    UserApi,
}
