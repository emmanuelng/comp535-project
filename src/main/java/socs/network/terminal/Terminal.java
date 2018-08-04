package socs.network.terminal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Terminal {

	private Object aInvokable;
	private String aPrompt;

	public Terminal(Object pInvokable, String pPrompt) {
		aInvokable = pInvokable;
		aPrompt = pPrompt;
	}

	public void start() {
		Scanner sc = new Scanner(System.in);

		try {
			while (true) {
				System.out.print(aPrompt + " ");
				String[] args = sc.nextLine().split("[ |\t]+");
				invoke(args[0], Arrays.copyOfRange(args, 1, args.length));
				System.out.println();
			}
		} catch (NoSuchElementException e) {
			// Ignore.
		} finally {
			sc.close();
		}
	}

	private void invoke(String methodName, String[] params) {
		for (Method method : aInvokable.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				Object[] castedParams = cast(params, method.getParameterTypes());
				if (castedParams != null) {
					try {
						method.invoke(aInvokable, castedParams);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		System.err.println("Invalid command.");
	}

	private Object[] cast(String[] params, Class<?>[] types) {
		if (params.length == types.length) {
			try {
				Object[] result = new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];

					if (types[i].equals(int.class)) {
						param = Integer.parseInt((String) param);
					} else if (types[i].equals(float.class)) {
						param = Float.parseFloat((String) param);
					} else if (types[i].equals(double.class)) {
						param = Double.parseDouble((String) param);
					} else if (types[i].equals(boolean.class)) {
						param = Boolean.parseBoolean((String) param);
					} else if (!types[i].equals(String.class)) {
						return null;
					}
					result[i] = param;
				}
				return result;
			} catch (Exception e) {
				// Return null
			}
		}

		return null;
	}
}
