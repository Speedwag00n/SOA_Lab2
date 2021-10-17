Vue.component(
    'result-table',
    {
        template:
            `
                <div>
                    <h1 class="col-xs-1 text-center">Loosers</h1>
                    <table class="table table-striped table-bordered table-hover p-5 text-center">
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Height</th>
                            <th>Passport ID</th>
                            <th>Nationality</th>
                        </tr>
                        <tr v-for="looser in loosers">
                            <td>{{ looser.id }}</td>
                            <td>{{ looser.name }}</td>
                            <td>{{ looser.height }}</td>
                            <td>{{ looser.passportId }}</td>
                            <td>{{ looser.nationality }}</td>
                        </tr>
                    </table>
                </div>
            `,

        props: ["loosers"]
    }
);

Vue.component(
    'humiliate-button',
    {
        template:
            `
                <div>
                    <h3>Humiliate by genre</h3>
                    <div>
                        <label for="genre">Genre:</label>
                        <input id="genre" type="text" maxlength="32" v-model="genre">
                        <button class="btn btn-danger" v-on:click="humiliate()" type="submit">Humiliate</button>
                    </div>
                </div>
            `,

        data: function() {
            return {
                genre: ''
            }
        },

        methods: {
            humiliate: function () {
                this.$emit('humiliate', this.genre);
            },
        }
    }
);
