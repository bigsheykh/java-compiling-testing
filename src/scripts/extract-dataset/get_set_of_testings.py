#!/usr/bin/env python3

import json
import git
import os


def get_set_of_testing():
    list_of_all = []
    list_of_unpassed = []
    repo_root = git.Repo('.', search_parent_directories=True).working_tree_dir
    data_set_path = os.path.join(repo_root, "dataset-infos", "dataset-info.json")
    with open(data_set_path) as json_file:
        data_set = json.load(json_file)
        for x in data_set:
            y = data_set[x]
            z = "test_results"
            results = y[z]
            # print(results)
            for version in results:
                if len(results[version]) > 2:
                    if results[version][1]:
                        if results[version][0]["passing"] + results[version][1]["passing"] + results[version][2]["passing"] > 300:
                            list_of_all.append((x, version, results[version], data_set[x]["repo_name"]))
                        if results[version][0]["error"] != 0 or results[version][0]["failing"] != 0:
                            list_of_unpassed.append((x, version, results[version], data_set[x]["repo_name"]))

    set_of_testing =  set([(x[3], x[1]) for x in list_of_all] + [(x[3], x[1]) for x in list_of_unpassed])
    return set_of_testing
