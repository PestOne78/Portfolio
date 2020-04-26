using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data;
using System.Windows;
using System.Windows.Controls;

namespace Person.DBUtils
{
    class DbQuery
    {
        private const string ListInfo = "all_info", ListPost = "list_post",
            ListDeps = "list_deps", ListStatus = "list_status",
            WherePost= "where_post", WhereDeps = "where_department", WhereStatus = "where_status",
            LikeName = "like_name", DateStart = "start_date", DateEnd="end_date", StartEndDate="start_end_date";


        //заполнение DataGrid
        public static List<PersonsModel> GetWhereList(short i, int n = 0, string m = "", string s ="")
        {
            List<PersonsModel> people = new List<PersonsModel>();
            try
            {
                using (MySqlConnection connection = Utils.GetDBConnection())
                {
                    MySqlCommand cmd = new MySqlCommand(ChooseSelect(i), connection);
                    cmd.CommandType = CommandType.StoredProcedure;

                    if (i == 0 || i == 5 || i == 6)
                        cmd.Parameters.AddWithValue("text_str", m);
                    else if (i == 1 || i == 2 || i == 3)
                        cmd.Parameters.AddWithValue("id", n);
                    else if (i == 7)
                    {
                        cmd.Parameters.AddWithValue("str_date", m);
                        cmd.Parameters.AddWithValue("en_date", s);
                    }

                    connection.Open();
                    using (MySqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            people.Add(new PersonsModel((string)reader["ФИО"], (string)reader["Дата устройства"].ToString(),
                                (string)reader["Дата увольнения"].ToString(), (string)reader["Статус"],
                                (string)reader["Отдел"], (string)reader["Должность"]));
                        }
                        reader.Close();
                    }
                    connection.Close();
                }
                return people;
            }
            catch (Exception)
            {
                MessageBox.Show("Вы не подключены к Базе Данных!", "Ошибка");
                return null;
            }
        }


        //выбор процедуры
        private static string ChooseSelect(short i)
        {
            string name = "";
            switch (i)
            {
                case 0:
                    name = LikeName;
                    break;
                case 1:
                    name = WherePost;
                    break;
                case 2:
                    name = WhereDeps;
                    break;
                case 3:
                    name = WhereStatus;
                    break;
                case 4:
                    name = ListInfo;
                    break;
                case 5:
                    name = DateStart;
                    break;
                case 6:
                    name = DateEnd;
                    break;
                case 7:
                    name = StartEndDate;
                    break;
            }
            return name;
        }


        //заполнение списков
        public static void getItemComboBox(ComboBox box)
        {
            string name = "";
            switch (box.Name)
            {
                case "Post_cb":
                    name = ListPost;
                    break;
                case "Department_cb":
                    name = ListDeps;
                    break;
                case "Status_cb":
                    name = ListStatus;
                    break;
            }
            try
            {
                using (MySqlConnection connection = Utils.GetDBConnection())
                {
                    connection.Open();

                    MySqlCommand cmd = new MySqlCommand(name, connection);
                    cmd.CommandType = CommandType.StoredProcedure;

                    using (MySqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            box.Items.Add(reader[0].ToString());
                        }
                        reader.Close();
                    }
                    connection.Close();
                }
            }
            catch (Exception e)
            {
                MessageBox.Show("Вы не подключены к Базе Данных!", "Ошибка");
            }
        }

    }
}
