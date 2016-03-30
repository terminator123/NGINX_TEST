//test
#include "test.h"
#include <time.h>

//初始化
struct test_data * test_init(char * conf_file) {
	struct test_data * test_data = 0;
	//
	if((test_data = (struct test_data *)malloc(sizeof(struct test_data))) == NULL || (test_data->data = (char *)malloc(strlen(conf_file) + 1)) == NULL) {
		fprintf(stderr, "%s: %d: %s: ERROR: malloc error\n", __FILE__, __LINE__, __FUNCTION__);
		exit(0);
	}
	//
	strcpy(test_data->data, conf_file);
	//
	return test_data;
}

//工作函数
char * test_work(struct test_data * test_data, char * args) {
	char * res = 0;
	//
	if((res = (char *)malloc(strlen(test_data->data) + strlen(args) + 64)) == NULL) {
		fprintf(stderr, "%s: %d: %s: ERROR: malloc error\n", __FILE__, __LINE__, __FUNCTION__);
		exit(0);
	}
	//
	res[0] = '\0';
	sprintf(res, "%s %lu %s", test_data->data, time(0), args);
	//
	return res;
}
