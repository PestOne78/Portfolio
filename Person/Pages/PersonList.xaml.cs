using System;

using System.Windows;
using System.Windows.Controls;
using Person.DBUtils;

namespace Person.Pages
{
   
    public partial class PersonList : Page
    {
        public PersonList()
        {
            InitializeComponent();

            GridOfPersons.ItemsSource = DbQuery.GetWhereList(4);
            DbQuery.getItemComboBox(Post_cb);
            DbQuery.getItemComboBox(Department_cb);
            DbQuery.getItemComboBox(Status_cb);
        }

        private void Post_cb_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Fio_tb.Text = "";
            int i = Post_cb.SelectedIndex + 1;
            GridOfPersons.ItemsSource = DbQuery.GetWhereList(1, i);
            lb_Summary.Visibility = Visibility.Hidden;
        }

        private void Department_cb_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Fio_tb.Text = "";
            int i = Department_cb.SelectedIndex + 1;
            GridOfPersons.ItemsSource = DbQuery.GetWhereList(2, i);
            lb_Summary.Visibility = Visibility.Hidden;
        }

        private void Status_cb_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Fio_tb.Text = "";
            string summ_name = "";
            int i = Status_cb.SelectedIndex + 1;
            GridOfPersons.ItemsSource = DbQuery.GetWhereList(3, i);
            switch (i)
            {
                case 1:
                    summ_name = "работающих: ";   
                    break;
                case 2:
                    summ_name = "уволенных: ";
                    break;
                case 3:
                    summ_name = "принятых: ";
                    break;
            }
            lb_Summary.Content = "Общее количество " + summ_name + GridOfPersons.Items.Count;
            lb_Summary.Visibility = Visibility.Visible;
        }

        private void Fio_tb_TextChanged(object sender, TextChangedEventArgs e)
        {
            GridOfPersons.ItemsSource = DbQuery.GetWhereList(0, m:Fio_tb.Text);
            lb_Summary.Visibility = Visibility.Hidden;
        }

        private void btn_SeeAll_Click(object sender, RoutedEventArgs e)
        {
            Post_cb.SelectedIndex = -1;
            Status_cb.SelectedIndex = -1;
            Department_cb.SelectedIndex = -1;
            Fio_tb.Text = "";
            lb_Summary.Visibility = Visibility.Hidden;
            GridOfPersons.ItemsSource = DbQuery.GetWhereList(4);
        }

        private void StartDate_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            if (EndDate.SelectedDate != null)
                GridOfPersons.ItemsSource = DbQuery.GetWhereList(7, m: GetStringDate(false), s: GetStringDate(true));
            else
                GridOfPersons.ItemsSource = DbQuery.GetWhereList(5, m: GetStringDate(false));
        }

        private void EndDate_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            if (StartDate.SelectedDate != null)
                GridOfPersons.ItemsSource = DbQuery.GetWhereList(7, m: GetStringDate(false), s: GetStringDate(true));
            else
                GridOfPersons.ItemsSource = DbQuery.GetWhereList(6, m: GetStringDate(true));
        }

        private string GetStringDate(Boolean i)
        {
            DateTime date;
            if (i)
                date = new DateTime(EndDate.SelectedDate.Value.Year,
                    EndDate.SelectedDate.Value.Month, EndDate.SelectedDate.Value.Day);
            else
                date = new DateTime(StartDate.SelectedDate.Value.Year,
                    StartDate.SelectedDate.Value.Month, StartDate.SelectedDate.Value.Day);

            return date.ToString("yyyy-MM-dd HH:mm:ss.ffffff");
        }
    }
}
