#include <iostream>
#include <fstream>
#include <chrono>
#include <random>

using namespace std;
using namespace std::chrono;

void random_number_generator(int n){
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(1, 1000);
    int numbers[] = {10000, 30000, 60000, 90000, 120000, 150000, 180000, 210000};
    for (int i = 0; i < n; i++){
        std::ofstream outfile("random_numbers"+ to_string(i) +".csv");
        for (int j = 0; j < numbers[i]; j++) {
            int random_number = dis(gen);
            outfile << random_number << "\n";
        }
        outfile.close();
    }
}

int main() {
    int n = 3; // Not more than 8
    random_number_generator(n);  
    for (int i = 0; i < n; i++){
        auto start = high_resolution_clock::now();
        ifstream infile("random_numbers"+ to_string(i) +".csv");
        int count = 0;
        double sum = 0;
        double value;
        while (infile >> value) {
            sum += value;
            count++;
        }
        double average = sum / count;
        cout << "Average: " << average << endl;
        infile.close();
        auto stop = high_resolution_clock::now();
        auto duration = duration_cast<milliseconds>(stop - start);
        cout << "Task time: " << duration.count() << " milliseconds" << endl;
    }
    return 0;
}
