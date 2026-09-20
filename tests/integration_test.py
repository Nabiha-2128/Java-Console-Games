"""Run after test.ps1 compilation. Python 3 standard library only."""
from pathlib import Path
import subprocess
import threading

ROOT = Path(__file__).resolve().parents[1]

def session(main, args, initial, expected, idle=False):
    proc = subprocess.Popen(['java', '-cp', 'out', main, *args], cwd=ROOT,
                            stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, text=True, encoding='utf-8')
    if not idle:
        output, _ = proc.communicate(initial, timeout=15)
    else:
        captured = []
        def collect():
            for line in proc.stdout:
                captured.append(line)
        reader = threading.Thread(target=collect, daemon=True)
        reader.start()
        proc.stdin.write(initial)
        proc.stdin.flush()  # Hold stdin OPEN: prove timeout does not depend on EOF or Enter.
        try:
            proc.wait(timeout=8)
        except subprocess.TimeoutExpired:
            proc.kill()
            raise AssertionError('Game did not end with idle input')
        reader.join(timeout=2)
        proc.stdin.close()
        output = ''.join(captured)
    assert proc.returncode == 0, output
    for phrase in expected:
        assert phrase in output, (phrase, output)
    assert 'Exception in thread' not in output, output
    return output

session('Main', [], 'bad\n0\n5\n', ['Enter a whole number', 'Thank you'])
session('Main', [], '', ['Input closed', 'Thank you'])
session('Main', [], '1\nTester\nq\n2\nTester\n1\nq\n3\nTester\nq\n4\n2\nA\nB\nq\n5\n',
        ['Treasure hunt cancelled', 'Quiz cancelled', 'Survival game cancelled', 'Board game cancelled', 'Thank you'])
session('Main', [], '2\nTester\n1\nAB\nA\nA\nA\nA\nA\n2\nTester\n3\nq\n5\n',
        ['Enter exactly A', 'Quiz completed', 'Answered: 5/5', 'Quiz cancelled', 'Thank you'])
session('ScenarioRunner', ['treasure-win'], 'Tester\n9\n1\n1\n4\n',
        ['Enter a whole number', 'already searched', 'Treasure found!', 'WORKERS STOPPED'])
session('ScenarioRunner', ['snake-win'], '2\nA\nB\n' + '\n' * 2000,
        ['wins!', 'WORKERS STOPPED'])
for scenario, initial, end in [
    ('treasure-timeout', 'Tester\n', 'Time is up!'),
    ('quiz-timeout', 'Tester\n1\n', 'Time is up!'),
    ('zombie-timeout', 'Tester\n', 'survived!'),
    ('zombie-death', 'Tester\n', 'Game over.')]:
    session('ScenarioRunner', [scenario], initial, [end, 'WORKERS STOPPED'], idle=True)
print('PASS: 10 integration scenarios (including real idle input and worker cleanup)')
