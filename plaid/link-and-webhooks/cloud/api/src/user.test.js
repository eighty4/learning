const {createConnectionPool} = require("./db");
const {UserApi} = require("./user")

describe('UserApi', () => {

    describe('emailLogin', () => {

        const email = 'asdf@asdf.com'

        let db, userApi

        beforeAll(() => {
            db = createConnectionPool()
            userApi = new UserApi(db)
        })

        beforeEach(async () => {
            await db.query('truncate eighty4_bank.users cascade')
        })

        afterAll(() => db.end())

        describe('happy paths', () => {

            it('saves login data for new user', async () => {
                const result = await userApi.emailLogin({email})

                const userId = result.userId
                const selected = await db.query('select * from eighty4_bank.users where user_id = $1', [userId])
                expect(selected.rows[0].email).toBe(email)
            })

            it('handles subsequent login', async () => {
                const first = await userApi.emailLogin({email})
                const second = await userApi.emailLogin({email})
                expect(first.email).toBe(second.email)
            })
        })

        describe('invalid request', () => {

            it('errors when missing email', async () => {
                try {
                    await userApi.emailLogin({})
                } catch (e) {
                    expect(e.message.indexOf('missing email')).not.toBe(-1)
                }
            })
        })
    })
})
